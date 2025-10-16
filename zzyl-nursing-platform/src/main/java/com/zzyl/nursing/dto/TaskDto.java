package com.zzyl.nursing.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskDto {

    /**
     * 护理任务ID
     */
    private Long taskId;

    /**
     * 取消理由
     */
    private String reason;

    /**
     * 执行时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime estimatedServerTime;

    /**
     * 任务图片
     */
    private String taskImage;

    /**
     * 执行记录
     */
    private String mark;

}
