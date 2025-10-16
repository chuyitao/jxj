package com.zzyl.nursing.vo;

import lombok.Data;

import java.util.List;

@Data
public class TreeVo {

    /**
     * id值
     */
    private String value;
    /**
     * 名称
     */
    private String label;
    /**
     * 子节点
     */
    private List<TreeVo> children;
}
