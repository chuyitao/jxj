package com.zzyl.nursing.config;

/**
 * API端点常量
 */
public class ApiEndpoints {
    public static final String CONVERSATIONS = ApiConfig.API_V1 + "/conversations";
    public static final String MESSAGES = ApiConfig.API_V1 + "/messages";
    public static final String CHAT_MESSAGES = ApiConfig.API_V1 + "/chat-messages";

    public static String conversationDetail(String chatId) {
        return CONVERSATIONS + "/" + chatId;
    }

    public static String messagesWithParams(String userId, String chatId) {
        return MESSAGES + "?user=" + userId + "&conversation_id=" + chatId;
    }

    public static String conversationsWithLimit(String userId) {
        return CONVERSATIONS + "?user=" + userId + "&limit=" + ApiConfig.CONVERSATION_LIMIT;
    }
}