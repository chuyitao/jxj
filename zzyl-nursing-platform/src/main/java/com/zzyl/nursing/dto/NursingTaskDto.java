package com.zzyl.nursing.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@ApiModel("护理任务DTO")
public class NursingTaskDto {

    /**
     * 页码
     */
    private Integer pageNum;

    /**
     * 每页显示条数
     */
    private Integer pageSize;

    /**
     * 老人姓名
     */
    private String elderName;

    /**
     * 护理员ID
     */
    private Long nurseId;

    /**
     * 护理项目ID
     */
    private Long projectId;

    /**
     * 开始时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    /**
     * 状态 (1待执行 2已执行 3已关闭)
     */
    private Integer status;
}
