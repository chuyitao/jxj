package com.zzyl.nursing.vo;

import lombok.Data;

import java.util.List;

@Data
public class RoomVo {

    /**
     * 房间ID
     */
    private Long id;

    /**
     * 楼层名称
     */
    private String floorName;

    /**
     * 楼层ID
     */
    private String floorId;

    /**
     * 房间ID
     */
    private String roomId;

    /**
     * 房间编号
     */
    private String code;

    /**
     * 房间价格
     */
    private String price;

    /**
     * 床位列表
     */
    private List<BedVo> bedVoList;

}