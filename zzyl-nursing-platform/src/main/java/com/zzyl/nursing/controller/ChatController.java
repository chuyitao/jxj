package com.zzyl.nursing.controller;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zzyl.common.core.domain.AjaxResult;
import com.zzyl.common.utils.SecurityUtils;
import com.zzyl.common.utils.StringUtils;
import com.zzyl.nursing.config.ApiConfig;
import com.zzyl.nursing.config.ApiEndpoints;
import com.zzyl.nursing.config.JsonFields;
import com.zzyl.nursing.vo.MessageVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI聊天控制器
 * 负责处理AI聊天相关的请求，包括对话历史管理和实时聊天
 */
@Slf4j
@RestController
@RequestMapping("/ai")
public class ChatController {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public ChatController(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.webClient = webClientBuilder
                .baseUrl(ApiConfig.BASE_URL)
                .defaultHeader(HttpHeaders.AUTHORIZATION, ApiConfig.BEARER_TOKEN)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
        this.objectMapper = objectMapper;
    }

    /**
     * 删除指定聊天ID的历史记录
     *
     * @param chatId 聊天ID
     * @return 操作结果
     */
    @DeleteMapping("/history/{chatId}")
    public AjaxResult deleteHistoryByChatId(@PathVariable String chatId) {
        try {
            Map<String, String> requestBody = createRequestBody();

            HttpResponse response = HttpUtil.createRequest(Method.DELETE,
                            buildFullUrl(ApiEndpoints.conversationDetail(chatId)))
                    .body(JSONUtil.toJsonStr(requestBody))
                    .header(HttpHeaders.AUTHORIZATION, ApiConfig.BEARER_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .execute();

            if (response.isOk()) {
                log.info("成功删除聊天历史，chatId: {}", chatId);
                return AjaxResult.success();
            } else {
                log.error("删除聊天历史失败，chatId: {}, 状态码: {}", chatId, response.getStatus());
                return AjaxResult.error("删除失败");
            }
        } catch (Exception e) {
            log.error("删除聊天历史时发生异常，chatId: {}", chatId, e);
            return AjaxResult.error("删除失败: " + e.getMessage());
        }
    }

    /**
     * 获取指定聊天ID的历史消息
     *
     * @param chatId 聊天ID
     * @return 历史消息列表
     */
    @GetMapping("/history/{chatId}")
    public AjaxResult getHistoryMessages(@PathVariable String chatId) {
        try {
            String url = buildFullUrl(ApiEndpoints.messagesWithParams(
                    SecurityUtils.getUserId().toString(), chatId));

            HttpResponse response = HttpUtil.createRequest(Method.GET, url)
                    .header(HttpHeaders.AUTHORIZATION, ApiConfig.BEARER_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .execute();

            if (response.isOk()) {
                List<MessageVO> data = JSONUtil.toList(
                        JSONUtil.toJsonStr(JSONUtil.parseObj(response.body()).get(JsonFields.DATA)),
                        MessageVO.class);
                log.debug("成功获取历史消息，chatId: {}, 消息数量: {}", chatId, data.size());
                return AjaxResult.success(data);
            } else {
                log.error("获取历史消息失败，chatId: {}, 状态码: {}", chatId, response.getStatus());
                return AjaxResult.error("获取历史消息失败");
            }
        } catch (Exception e) {
            log.error("获取历史消息时发生异常，chatId: {}", chatId, e);
            return AjaxResult.error("获取历史消息失败: " + e.getMessage());
        }
    }

    /**
     * 获取当前用户的对话列表
     *
     * @return 对话列表
     */
    @GetMapping("/history")
    public AjaxResult getConversations() {
        try {
            String url = buildFullUrl(ApiEndpoints.conversationsWithLimit(
                    SecurityUtils.getUserId().toString()));

            HttpResponse response = HttpUtil.createRequest(Method.GET, url)
                    .header(HttpHeaders.AUTHORIZATION, ApiConfig.BEARER_TOKEN)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .execute();

            if (response.isOk()) {
                Object data = JSONUtil.parseArray(JSONUtil.parseObj(response.body()).get(JsonFields.DATA));
                log.debug("成功获取对话列表，用户ID: {}", SecurityUtils.getUserId());
                return AjaxResult.success(data);
            } else {
                log.error("获取对话列表失败，用户ID: {}, 状态码: {}",
                        SecurityUtils.getUserId(), response.getStatus());
                return AjaxResult.error("获取对话列表失败");
            }
        } catch (Exception e) {
            log.error("获取对话列表时发生异常，用户ID: {}", SecurityUtils.getUserId(), e);
            return AjaxResult.error("获取对话列表失败: " + e.getMessage());
        }
    }

    /**
     * AI聊天接口（流式响应）
     *
     * @param prompt 用户输入
     * @param chatId 聊天ID（可选）
     * @return 流式响应数据
     */
    @PostMapping(value = "/chat", produces = MediaType.TEXT_PLAIN_VALUE)
    public Flux<String> chat(@RequestParam String prompt,
                             @RequestParam(required = false) String chatId) {
        Map<String, Object> requestBody = createChatRequestBody(prompt, chatId);

        log.debug("开始AI聊天请求，用户ID: {}, chatId: {}", SecurityUtils.getUserId(), chatId);

        return webClient.post()
                .uri(ApiEndpoints.CHAT_MESSAGES)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToFlux(String.class)
                .filter(data -> StringUtils.isNotEmpty(data) && !data.trim().isEmpty())
                .flatMap(this::processSSEData)
                .doOnNext(answer -> log.trace("AI响应片段: {}", answer))
                .doOnComplete(() -> log.debug("AI聊天流式响应完成"))
                .doOnCancel(() -> log.warn("客户端取消了AI聊天请求"))
                .onErrorResume(this::handleChatError);
    }

    /**
     * 创建基础请求体（包含用户信息）
     */
    private Map<String, String> createRequestBody() {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put(JsonFields.USER, SecurityUtils.getUserId().toString());
        return requestBody;
    }

    /**
     * 创建聊天请求体
     */
    private Map<String, Object> createChatRequestBody(String prompt, String chatId) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put(JsonFields.QUERY, prompt);
        requestBody.put(JsonFields.INPUTS, new HashMap<>());
        requestBody.put(JsonFields.RESPONSE_MODE, ApiConfig.RESPONSE_MODE_STREAMING);
        requestBody.put(JsonFields.USER, SecurityUtils.getUserId().toString());

        if (StringUtils.isNotEmpty(chatId) && !"null".equals(chatId)) {
            requestBody.put(JsonFields.CONVERSATION_ID, chatId);
        }

        return requestBody;
    }

    /**
     * 处理SSE格式数据，提取answer/text字段
     */
    private Mono<String> processSSEData(String sseData) {
        try {
            String jsonData = extractJsonData(sseData);

            if (ApiConfig.SSE_DONE.equals(jsonData)) {
                return Mono.empty();
            }

            JsonNode jsonNode = objectMapper.readTree(jsonData);
            String content = extractContentFromJson(jsonNode);

            return StringUtils.isNotEmpty(content) ? Mono.just(content) : Mono.empty();

        } catch (Exception e) {
            log.warn("解析SSE数据失败: {}, 原始数据: {}", e.getMessage(), sseData);
            return Mono.empty();
        }
    }

    /**
     * 从SSE数据中提取JSON数据
     */
    private String extractJsonData(String sseData) {
        if (sseData.startsWith(ApiConfig.SSE_DATA_PREFIX)) {
            return sseData.substring(ApiConfig.SSE_DATA_PREFIX.length()).trim();
        }
        return sseData;
    }

    /**
     * 从JSON节点中提取内容
     */
    private String extractContentFromJson(JsonNode jsonNode) {
        // 优先提取answer字段
        JsonNode answerNode = jsonNode.get(JsonFields.ANSWER);
        if (isValidJsonNode(answerNode)) {
            return answerNode.asText();
        }

        // 其次提取text字段
        JsonNode textNode = jsonNode.get(JsonFields.TEXT);
        if (isValidJsonNode(textNode)) {
            return textNode.asText();
        }

        return null;
    }

    /**
     * 检查JSON节点是否有效
     */
    private boolean isValidJsonNode(JsonNode node) {
        return node != null && !node.isNull() && StringUtils.isNotEmpty(node.asText());
    }

    /**
     * 处理聊天错误
     */
    private Mono<String> handleChatError(Throwable e) {
        log.error("AI聊天服务错误", e);
        return Mono.just("抱歉，服务暂时不可用，请稍后重试。");
    }

    /**
     * 构建完整URL
     */
    private String buildFullUrl(String endpoint) {
        return ApiConfig.BASE_URL + endpoint;
    }
}