package com.zzyl.nursing.vo;

import com.zzyl.nursing.domain.NursingProjectPlan;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class NursingPlanVo {

    /**
     * 护理计划id
     */
    private Long id;

    /**
     * 排序号
     */
    private Integer sortNo;
    /**
     * 计划名称
     */
    private String planName;

    /**
     * 状态，0：禁用，1：启用
     */
    private Integer status;

    /**
     * 项目计划
     */
    List<NursingProjectPlanVo> projectPlans;

}