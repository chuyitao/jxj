package com.zzyl.test;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import org.springframework.http.HttpHeaders;

public class MainTest {

    public static void main(String[] args) {
        String url = "http://localhost/v1/conversations?user=1&limit=1";
        HttpResponse response = HttpUtil.createRequest(Method.GET, url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer app-CugsTPcFHPdVHsug2KmrErqe")
                .execute();
        System.out.println(response.body());
    }
}
