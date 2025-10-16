package com.zzyl.nursing.vo;

import com.zzyl.nursing.domain.NursingTask;
import lombok.Data;

import java.util.List;

@Data
public class NursingTaskVo extends NursingTask {

    /**
     * 护理等级名称
     */
    private String nursingLevelName;
    /**
     * 年龄
     */
    private Integer age;
    /**
     * 护理名称
     */
    private List<String> nursingName;

    /**
     * 性别
     */
    private String sex;

    /**
     * 执行人
     */
    private String updater;

}
