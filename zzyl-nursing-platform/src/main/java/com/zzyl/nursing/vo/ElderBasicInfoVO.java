package com.zzyl.nursing.vo;

import lombok.Data;

@Data
public class ElderBasicInfoVO {

    private Long elderId;

    private String name;

    private Integer age;

    private Integer sex;

    private String sexName;

    private String checkInTime;

    private String roomNumber;

    private String bedNumber;

    private String nursingLevelName;

    private String phone;

    private String address;
}
