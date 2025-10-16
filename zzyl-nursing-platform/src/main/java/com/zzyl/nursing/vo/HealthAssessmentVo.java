package com.zzyl.nursing.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zzyl.common.annotation.Excel;
import com.zzyl.nursing.domain.HealthAssessmentDetail;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class HealthAssessmentVo extends HealthAssessmentDetail {

    /** 主键 */
    private Long id;

    /** 老人姓名 */
    @Excel(name = "老人姓名")
    private String elderName;

    /** 身份证号 */
    @Excel(name = "身份证号")
    private String idCard;

    /** 健康评分 */
    @Excel(name = "健康评分")
    private String healthScore;

    /** 是否建议入住(0:建议，1:不建议) */
    @Excel(name = "是否建议入住(0:建议，1:不建议)")
    private Integer suggestionForAdmission;

    /** 推荐护理等级 */
    @Excel(name = "推荐护理等级")
    private String nursingLevelName;

    /** 入住情况(0:已入住，1:未入住) */
    @Excel(name = "入住情况(0:已入住，1:未入住)")
    private Integer admissionStatus;

    /** 总检日期 */
    @Excel(name = "总检日期")
    private String totalCheckDate;

    /** 评估时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "评估时间", width = 30, dateFormat = "yyyy-MM-dd")
    private LocalDateTime assessmentTime;
}
