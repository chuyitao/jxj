package com.zzyl.nursing.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzyl.common.core.page.TableDataInfo;
import com.zzyl.common.exception.base.BaseException;
import com.zzyl.common.utils.DateUtils;
import com.zzyl.common.utils.UserThreadLocal;
import com.zzyl.nursing.domain.Reservation;
import com.zzyl.nursing.dto.ReservationDto;
import com.zzyl.nursing.dto.ReservationQueryDto;
import com.zzyl.nursing.mapper.ReservationMapper;
import com.zzyl.nursing.service.IReservationService;
import com.zzyl.nursing.vo.ElderVisitInfoVO;
import com.zzyl.nursing.vo.TimeCountVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 预约信息Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-07-25
 */
@Service
public class ReservationServiceImpl extends ServiceImpl<ReservationMapper,Reservation> implements IReservationService
{
    @Autowired
    private ReservationMapper reservationMapper;

    @Override
    public String getReservationByDay(LocalDate dateTime) {

        if(ObjectUtil.isEmpty(dateTime)){
            dateTime = LocalDate.now();
        }

        List<Reservation> list = reservationMapper.selectByNow(dateTime);
        if(null != list && !list.isEmpty()){

            List<ElderVisitInfoVO> resultList = new ArrayList<>();
            for (Reservation reservation : list) {
                ElderVisitInfoVO vo = new ElderVisitInfoVO();
                String dateStr = reservation.getTime().toString();
                vo.setVisitTime(dateStr);
                vo.setVisitor(reservation.getName());
                vo.setName(reservation.getVisitor());
                resultList.add(vo);
            }
            return JSONUtil.toJsonStr(resultList);
        }

        return null;
    }

    /**
     * 更新预约状态  每小时的1分钟和31分钟执行一次
     */
    @Override
    public void updateReservationStatus() {
        //查询出符合更新状态的数据
        LocalDateTime now = LocalDateTime.now();
        List<Reservation>  list = reservationMapper.selectByTime(now);
        if(CollUtil.isNotEmpty(list)){
            list.forEach(reservation -> {
                reservation.setStatus(3);
            });
            updateBatchById(list);
        }

    }

    /**
     * 取消预约
     * @param id
     */
    @Override
    public void cancelReservation(Long id) {
        //更新状态  为  2
        Reservation reservation = selectReservationById(id);
        if(null == reservation){
            throw  new BaseException("当前预约不存在");
        }
        reservation.setStatus(2);
        reservation.setUpdateBy(UserThreadLocal.getUserId()+"");
        reservationMapper.updateReservation(reservation);

    }

    /**
     * 分页查询预约信息
     * @param pageNum
     * @param pageSize
     * @param status
     * @return
     */
    @Override
    public TableDataInfo selectByPage(Integer pageNum, Integer pageSize, Integer status) {

        //条件是什么？  status  当前登录人

        Long userId = UserThreadLocal.getUserId();

        //查询总记录数
        Integer count = reservationMapper.count(status,userId);

        //分页查询  pageNum  pageSize
        //计算起始索引
        Integer startIndex = (pageNum - 1) * pageSize;
        List<Reservation>  list = reservationMapper.selectByPage(startIndex,pageSize,status,userId);

        return new TableDataInfo<Reservation>(list,count);
    }

    /**
     * 查询预约数量
     *
     * @param time
     * @return
     */
    @Override
    public List<TimeCountVo> getCountByTime(Long time) {
        //通过时间戳转换为LocalDateTime
        LocalDateTime localDateTime = LocalDateTimeUtil.of(time);
        //获取开始和结束时间
        LocalDateTime startTime = localDateTime.toLocalDate().atStartOfDay();
        LocalDateTime endTime = startTime.plusHours(24);
        //查询数据库
        List<TimeCountVo> list = reservationMapper.getCountByTime(startTime,endTime);
        return list;
    }

    //Crtl + i


    /**
     * 查询当前取消预约数量
     *
     * @param userId
     * @return
     */
    @Override
    public Integer cancelledCount(Long userId) {

        //根据用户id查询当前用户当天取消的次数

        // 2025-07-26 00:00:00  2025-07-27 00:00:00
        LocalDateTime startTime = LocalDate.now().atStartOfDay();
        LocalDateTime endTime = startTime.plusHours(24);
        //查询数据库
        Integer count = reservationMapper.cancelledCount(userId,startTime,endTime);

        return count;
    }

    /**
     * 查询预约信息
     * 
     * @param id 预约信息主键
     * @return 预约信息
     */
    @Override
    public Reservation selectReservationById(Long id)
    {
        return reservationMapper.selectReservationById(id);
    }

    /**
     * 查询预约信息列表
     * 
     * @param reservation 预约信息
     * @return 预约信息
     */
    @Override
    public List<Reservation> selectReservationList(ReservationQueryDto reservation)
    {
        return reservationMapper.selectReservationList(reservation);
    }

    /**
     * 新增预约信息
     * 
     * @param dto 预约信息
     * @return 结果
     */
    @Override
    public int insertReservation(ReservationDto dto)
    {

        //保存预约信息
        //判断当前选中的时间是否已经约满了？
        LocalDateTime time = dto.getTime();
        Integer count = reservationMapper.countByTime(time);
        if(count >= 6){
            throw new BaseException("当前时段已约满");
        }

        //同一个手机号在同一时间只能预约一次
        Integer ct = reservationMapper.countByTimeAndMobile(dto.getTime(),dto.getMobile());
        if(ct > 0){
            throw new BaseException("当前手机号已预约");
        }


        //要知道是谁预约的
        Long userId = UserThreadLocal.getUserId();

        //对象拷贝
        Reservation reservation = BeanUtil.toBean(dto, Reservation.class);
        reservation.setCreateBy(userId+"");
        reservation.setUpdateBy(userId+"");

        //设置预约状态   0 待报道
        reservation.setStatus(0);

        //更新时间，创建时间
        reservation.setCreateTime(DateUtils.getNowDate());
        reservation.setUpdateTime(DateUtils.getNowDate());
        return reservationMapper.insertReservation(reservation);
    }

    /**
     * 修改预约信息
     * 
     * @param reservation 预约信息
     * @return 结果
     */
    @Override
    public int updateReservation(Reservation reservation)
    {
        reservation.setUpdateTime(DateUtils.getNowDate());
        return reservationMapper.updateReservation(reservation);
    }

    /**
     * 批量删除预约信息
     * 
     * @param ids 需要删除的预约信息主键
     * @return 结果
     */
    @Override
    public int deleteReservationByIds(Long[] ids)
    {
        return reservationMapper.deleteReservationByIds(ids);
    }

    /**
     * 删除预约信息信息
     * 
     * @param id 预约信息主键
     * @return 结果
     */
    @Override
    public int deleteReservationById(Long id)
    {
        return reservationMapper.deleteReservationById(id);
    }
}
