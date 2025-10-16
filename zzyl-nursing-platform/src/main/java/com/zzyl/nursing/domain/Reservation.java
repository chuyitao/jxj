package com.zzyl.nursing.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zzyl.common.annotation.Excel;
import com.zzyl.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.LocalDateTime;

/**
 * 预约信息对象 reservation
 * 
 * @author ruoyi
 * @date 2025-07-25
 */
public class Reservation extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 预约人姓名 */
    @Excel(name = "预约人姓名")
    private String name;

    /** 预约人手机号 */
    @Excel(name = "预约人手机号")
    private String mobile;

    /** 预约时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "预约时间", width = 30, dateFormat = "yyyy-MM-dd")
    private LocalDateTime time;

    /** 探访人 */
    @Excel(name = "探访人")
    private String visitor;

    /** 预约类型，0：参观预约，1：探访预约 */
    @Excel(name = "预约类型，0：参观预约，1：探访预约")
    private Integer type;

    /** 预约状态，0：待报道，1：已完成，2：取消，3：过期 */
    @Excel(name = "预约状态，0：待报道，1：已完成，2：取消，3：过期")
    private Integer status;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }

    public void setMobile(String mobile) 
    {
        this.mobile = mobile;
    }

    public String getMobile() 
    {
        return mobile;
    }

    public void setTime(LocalDateTime time) 
    {
        this.time = time;
    }

    public LocalDateTime getTime() 
    {
        return time;
    }

    public void setVisitor(String visitor) 
    {
        this.visitor = visitor;
    }

    public String getVisitor() 
    {
        return visitor;
    }

    public void setType(Integer type) 
    {
        this.type = type;
    }

    public Integer getType() 
    {
        return type;
    }

    public void setStatus(Integer status) 
    {
        this.status = status;
    }

    public Integer getStatus() 
    {
        return status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("name", getName())
            .append("mobile", getMobile())
            .append("time", getTime())
            .append("visitor", getVisitor())
            .append("type", getType())
            .append("status", getStatus())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("createBy", getCreateBy())
            .append("updateBy", getUpdateBy())
            .append("remark", getRemark())
            .toString();
    }
}
