package com.zzyl.nursing.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.zzyl.common.exception.base.BaseException;
import com.zzyl.nursing.service.WechatService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class WechatServiceImpl implements WechatService {

    //获取openid
    private static final String OPENID_URL = "https://api.weixin.qq.com/sns/jscode2session?grant_type=authorization_code";

    //获取token
    private static final String TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential";

    //获取手机号
    private static final String PHONE_URL = "https://api.weixin.qq.com/wxa/business/getuserphonenumber?access_token=";


    @Value("${wechat.appid}")
    private String appid;

    @Value("${wechat.secret}")
    private String secret;

    /**
     * 获取openid
     *
     * @param code
     * @return
     */
    @Override
    public String getOpenid(String code) {
        //构建参数
        Map<String, Object> params = getParams();
        params.put("js_code",code);
        //发起请求
        String result = HttpUtil.get(OPENID_URL, params);
        //是一个map
        JSONObject jsonObject = JSONUtil.parseObj(result);
        if(ObjectUtil.isNotEmpty(jsonObject.getStr("errcode"))){
            throw new BaseException(jsonObject.getStr("errmsg"));
        }

        return jsonObject.getStr("openid");
    }

    private String getToken(){
        Map<String,Object> params = getParams();
        String result = HttpUtil.get(TOKEN_URL, params);

        //是一个map
        JSONObject jsonObject = JSONUtil.parseObj(result);
        if(ObjectUtil.isNotEmpty(jsonObject.getStr("errcode"))){
            throw new RuntimeException(jsonObject.getStr("errmsg"));
        }

        return jsonObject.getStr("access_token");
    }

    /**
     * 获取手机号
     *
     * @param detailCode
     * @return
     */
    @Override
    public String getPhone(String detailCode) {
        //拼接url
        String token = getToken();
        String url = PHONE_URL + token;
        //构建参数，由于是post请求，必须把参数转换为json发送
        Map<String,Object> params = new HashMap<>();
        params.put("code",detailCode);
        //发起请求
        String result = HttpUtil.post(url, JSONUtil.toJsonStr(params));
        //是一个map
        JSONObject jsonObject = JSONUtil.parseObj(result);
        if(jsonObject.getInt("errcode") != 0){
            throw new RuntimeException(jsonObject.getStr("errmsg"));
        }
        //获取手机号
        return jsonObject.getJSONObject("phone_info").getStr("phoneNumber");
    }

    private Map<String,Object> getParams(){
        Map<String,Object> params = new HashMap<>();
        params.put("appid",appid);
        params.put("secret",secret);
        return params;
    }
}
