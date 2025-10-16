package com.zzyl.nursing.job;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class HelloJob {

//    @Scheduled(cron = "0/5 * * * * ?")
    public void hello(){
        System.out.println("hello方法执行了..."+ LocalDateTime.now());
    }
}
