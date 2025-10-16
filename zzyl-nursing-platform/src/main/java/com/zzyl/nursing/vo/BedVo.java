package com.zzyl.nursing.vo;

import com.zzyl.common.core.domain.entity.SysUser;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class BedVo {


    private Long id;

    /**
     * 床位号
     */
    private String bedNumber;

    /**
     * 床位状态: 未入住0, 已入住1 入住申请中2
     */
    private Integer bedStatus;

    /**
     * 房间ID
     */
    private Long roomId;

    /**
     * 老人姓名
     */
    private String ename;

    /**
     * 老人id
     */
    private Long elderId;


    private List<SysUser> userVos;
}
