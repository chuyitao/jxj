package com.zzyl.nursing.domain;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import com.zzyl.common.annotation.Excel;
import com.zzyl.common.core.domain.BaseEntity;

/**
 * 健康评估详情对象 health_assessment_detail
 * 
 * @author ruoyi
 * @date 2025-08-22
 */
@Data
public class HealthAssessmentDetail extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 健康评估id */
    @Excel(name = "健康评估id")
    private Long healthAssessmentId;

    /** 出生日期 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "出生日期", width = 30, dateFormat = "yyyy-MM-dd")
    private LocalDateTime birthDate;

    /** 年龄 */
    @Excel(name = "年龄")
    private Integer age;

    /** 性别(0:男，1:女) */
    @Excel(name = "性别(0:男，1:女)")
    private Integer gender;

    /** 严重危险(健康, 提示, 风险, 危险, 严重危险) */
    @Excel(name = "严重危险(健康, 提示, 风险, 危险, 严重危险)")
    private String riskLevel;

    /** 体检机构 */
    @Excel(name = "体检机构")
    private String physicalExamInstitution;

    /** 体检报告URL链接 */
    @Excel(name = "体检报告URL链接")
    private String physicalReportUrl;

    /** 报告总结 */
    @Excel(name = "报告总结")
    private String reportSummary;

    /** 异常分析 */
    @Excel(name = "异常分析")
    private String abnormalAnalysis;

    /** 健康系统分值 */
    @Excel(name = "健康系统分值")
    private String systemScore;



}
