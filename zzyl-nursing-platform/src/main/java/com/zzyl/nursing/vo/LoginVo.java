package com.zzyl.nursing.vo;

import lombok.Data;

/**
 * LoginVO
 * @author itheima
 */
@Data
public class LoginVo {

    /**
     * JWT token
     */
    private String token;

    /**
     * 昵称
     */
    private String nickName;
}