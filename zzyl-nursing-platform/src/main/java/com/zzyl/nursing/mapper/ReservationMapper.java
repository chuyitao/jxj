package com.zzyl.nursing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zzyl.nursing.domain.Reservation;
import com.zzyl.nursing.dto.ReservationQueryDto;
import com.zzyl.nursing.vo.TimeCountVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 预约信息Mapper接口
 * 
 * @author ruoyi
 * @date 2025-07-25
 */
@Mapper
public interface ReservationMapper extends BaseMapper<Reservation>
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
    public int insertReservation(Reservation reservation);

    /**
     * 修改预约信息
     * 
     * @param reservation 预约信息
     * @return 结果
     */
    public int updateReservation(Reservation reservation);

    /**
     * 删除预约信息
     * 
     * @param id 预约信息主键
     * @return 结果
     */
    public int deleteReservationById(Long id);

    /**
     * 批量删除预约信息
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteReservationByIds(Long[] ids);

    Integer cancelledCount(@Param("userId") Long userId,@Param("startTime") LocalDateTime startTime, @Param("endTime")LocalDateTime endTime);

    List<TimeCountVo> getCountByTime(@Param("startTime")LocalDateTime startTime, @Param("endTime")LocalDateTime endTime);

    @Select("select count(*) from reservation where time = #{time}")
    Integer countByTime(LocalDateTime time);

    @Select("select count(*) from reservation where status = #{status} and create_by = #{userId}")
    Integer count(@Param("status") Integer status, @Param("userId")Long userId);

    List<Reservation> selectByPage(@Param("startIndex")Integer startIndex, @Param("pageSize")Integer pageSize, @Param("status") Integer status, @Param("userId")Long userId);

    @Select("select * from reservation where time <= #{now} and status = 0")
    List<Reservation> selectByTime(LocalDateTime now);

    @Select("select * from reservation where visitor = #{name} and date_format(time,'%Y-%m-%d') = #{now}")
    List<Reservation> selectByTimeAndVisit(@Param("name")String name, @Param("now")LocalDate now);

    @Select("select * from reservation where date_format(time,'%Y-%m-%d') = #{now}")
    List<Reservation> selectByNow(LocalDate now);

    @Select("select count(*) from reservation where time = #{time} and mobile = #{mobile} and status = 0")
    Integer countByTimeAndMobile(@Param("time")LocalDateTime time, @Param("mobile")String mobile);
}
