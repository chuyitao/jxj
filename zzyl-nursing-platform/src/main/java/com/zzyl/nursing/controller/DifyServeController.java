package com.zzyl.nursing.controller;

import com.zzyl.nursing.service.IElderService;
import com.zzyl.nursing.service.IHealthAssessmentService;
import com.zzyl.nursing.service.IReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/dify/serve")
public class DifyServeController {

    @Autowired
    private IReservationService reservationService;

    @Autowired
    private IElderService elderService;

    @Autowired
    private IHealthAssessmentService healthAssessmentService;

    @GetMapping("/getReservationByToday")
    public String getReservationByDay(String datetime){
        System.out.println(datetime);

        // 定义日期时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        // 字符串转 LocalDateTime
        LocalDate dateTime = LocalDate.parse(datetime, formatter);

        return reservationService.getReservationByDay(dateTime);

    }

    /**
     * 查询老人基本信息
     *
     * @param nameOrId 老人姓名或ID
     */
    @GetMapping("/elder/basic-info")
    public String getElderBasicInfo(String nameOrId)
    {
        return elderService.getElderBasicInfo(nameOrId);
    }

    /**
     * 查询老人健康信息
     *
     * @param nameOrId 老人姓名或ID
     */
    @GetMapping("/elder/health-info")
    public String getElderHealthInfo(String nameOrId)
    {
        return healthAssessmentService.getElderHealthInfo(nameOrId);
    }

}
