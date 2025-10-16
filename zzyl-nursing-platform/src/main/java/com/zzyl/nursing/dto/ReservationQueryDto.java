package com.zzyl.nursing.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class ReservationQueryDto {

    /**
     * 预约人
     */
    @ApiModelProperty("预约人")
    private String name;

    /**
     * 预约人手机号
     */
    @ApiModelProperty("预约人手机号")
    private String mobile;

    /**
     * 时间
     */
    @ApiModelProperty("开始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    /**
     * 时间
     */
    @ApiModelProperty("结束时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    /**
     * 预约状态，0：待报道，1：已完成，2：取消，3：过期
     */
    @ApiModelProperty("预约状态，0：待报道，1：已完成，2：取消，3：过期")
    private Integer status;

    /**
     * 预约类型，0：参观预约，1：探访预约
     */
    @ApiModelProperty("预约类型，0：参观预约，1：探访预约")
    private Integer type;


}