package com.zzyl.nursing.dto;

import lombok.Data;

@Data
public class HealthAssessmentDto {

    /**
     * 老人姓名
     */
    private String elderName;

    /**
     * 身份证号
     */
    private String idCard;

    /**
     * 体检机构
     */
    private String physicalExamInstitution;

    /**
     * 体检报告URL链接
     */
    private String physicalReportUrl;

}