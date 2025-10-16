package com.zzyl.nursing.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * C端用户登录
 */
@Data
public class UserLoginRequestDto {

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 登录临时凭证
     */
    private String code;

    /**
     * 手机号临时凭证
     */
    private String phoneCode;
}