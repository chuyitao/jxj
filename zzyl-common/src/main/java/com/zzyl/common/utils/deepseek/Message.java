package com.zzyl.common.utils.deepseek;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Message {
    public String role;
    public String content;
    public static Message userMessage(String content) {
        return new Message("user", content);
    }

    public static Message assistantMessage(String content) {
        return new Message("assistant", content);
    }
}