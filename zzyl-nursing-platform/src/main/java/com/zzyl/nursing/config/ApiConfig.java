package com.zzyl.nursing.config;

/**
 * API配置常量
 */
public class ApiConfig {
    // 基础URL
    public static final String BASE_URL = "http://192.168.150.103";
    // 认证令牌
    public static final String BEARER_TOKEN = "Bearer app-C98XuttsLF1w9SjetjwaXINQ";
    // API版本路径
    public static final String API_V1 = "/v1";
    // 对话历史限制
    public static final int CONVERSATION_LIMIT = 20;
    // 流式响应模式
    public static final String RESPONSE_MODE_STREAMING = "streaming";
    // SSE数据前缀
    public static final String SSE_DATA_PREFIX = "data: ";
    // SSE结束标识
    public static final String SSE_DONE = "[DONE]";
}