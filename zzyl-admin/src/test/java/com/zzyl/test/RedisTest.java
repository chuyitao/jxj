package com.zzyl.test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
public class RedisTest {

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Test
    public void test(){
        //set name zhangsan     get name
        redisTemplate.opsForValue().set("name","zhangsan");

        System.out.println(redisTemplate.opsForValue().get("name"));

    }
}
