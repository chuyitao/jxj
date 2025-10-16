package com.zzyl.common.utils.deepseek;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ChatRequest {
    private String model;
    private Boolean stream;
    private List<Message> messages;
}