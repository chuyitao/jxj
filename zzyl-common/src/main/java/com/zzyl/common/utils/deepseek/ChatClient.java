package com.zzyl.common.utils.deepseek;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ChatClient {
    private String baseUrl;
    private String apiKey;
    private String model;
    private String defaultSystem;


    public String chat(String prompt) {
        // 1.创建请求体对象
        ChatRequest request = ChatRequest.builder()
                .model(model)
                .messages(List.of(
                        Message.assistantMessage(defaultSystem),
                        Message.userMessage(prompt)
                ))
                .stream(false)
                .build();


        // 2.调用DeepSeek
        HttpResponse response = HttpRequest.post(baseUrl)
                .bearerAuth(apiKey)
                .body(JSONUtil.toJsonStr(request)).execute();
        //3.解析结果
        if (response.isOk()) {
            // 1.获取响应体
            String body = response.body();
            // 2.将响应体转为JSON对象
            JSONObject jsonObject = JSONUtil.parseObj(body);
            // 3.获取内容
            String content = jsonObject.getByPath("choices[0].message.content", String.class);
            return content;
        }

        return null;
    }
}