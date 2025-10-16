package com.zzyl.nursing.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zzyl.common.core.page.TableDataInfo;
import com.zzyl.nursing.domain.Reservation;
import com.zzyl.nursing.dto.ReservationDto;
import com.zzyl.nursing.dto.ReservationQueryDto;
import com.zzyl.nursing.vo.TimeCountVo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 预约信息Service接口
 * 
 * @author ruoyi
 * @date 2025-07-25
 */
public interface IReservationService extends IService<Reservation>
{
    /**
     * 查询预约信息
     * 
     * @param id 预约信息主键
     * @return 预约信息
     */
    public Reservation selectReservationById(Long id);

    /**
     * 查询预约信息列表
     * 
     * @param reservation 预约信息
     * @return 预约信息集合
     */
    public List<Reservation> selectReservationList(ReservationQueryDto reservation);

    /**
     * 新增预约信息
     * 
     * @param reservation 预约信息
     * @return 结果
     */
    public int insertReservation(ReservationDto reservation);

    /**
     * 修改预约信息
     * 
     * @param reservation 预约信息
     * @return 结果
     */
    public int updateReservation(Reservation reservation);

    /**
     * 批量删除预约信息
     * 
     * @param ids 需要删除的预约信息主键集合
     * @return 结果
     */
    public int deleteReservationByIds(Long[] ids);

    /**
     * 删除预约信息信息
     * 
     * @param id 预约信息主键
     * @return 结果
     */
    public int deleteReservationById(Long id);

    /**
     * 查询当前取消预约数量
     * @param userId
     * @return
     */
    Integer cancelledCount(Long userId);

    /**
     * 查询预约数量
     * @param time
     * @return
     */
    List<TimeCountVo> getCountByTime(Long time);

    /**
     * 分页查询预约信息
     * @param pageNum
     * @param pageSize
     * @param status
     * @return
     */
    TableDataInfo selectByPage(Integer pageNum, Integer pageSize, Integer status);

    /**
     * 取消预约
     * @param id
     */
    void cancelReservation(Long id);

    /**
     * 更新预约状态
     */
    public void updateReservationStatus();

    String getReservationByDay(LocalDate dateTime);
}
