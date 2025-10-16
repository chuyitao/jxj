package com.zzyl.nursing.controller.member;

import com.zzyl.common.core.domain.AjaxResult;
import com.zzyl.common.core.domain.R;
import com.zzyl.common.core.page.TableDataInfo;
import com.zzyl.common.utils.UserThreadLocal;
import com.zzyl.nursing.domain.Reservation;
import com.zzyl.nursing.dto.ReservationDto;
import com.zzyl.nursing.service.IReservationService;
import com.zzyl.nursing.vo.TimeCountVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/member/reservation")  //公共路径
public class MemberReservationController {

    @Autowired
    private IReservationService reservationService;


    @GetMapping("/cancelled-count")
    public AjaxResult cancelledCount(){
        //查询当前用户当天取消的次数

        //从当前线程中获取登录的用户id
        Long userId = UserThreadLocal.getUserId();
        Integer count = reservationService.cancelledCount(userId);
        return AjaxResult.success(count);
    }

    @GetMapping("/countByTime")
    public R<List<TimeCountVo>> getCountByTime(Long time){
        List<TimeCountVo> list = reservationService.getCountByTime(time);
        return R.ok(list);
    }

    @PostMapping
    public AjaxResult insertReservation(@RequestBody ReservationDto reservationDto){

        int count = reservationService.insertReservation(reservationDto);
        return AjaxResult.success(count);
    }

    @GetMapping("/page")
    public AjaxResult selectByPage(@RequestParam(defaultValue = "1") Integer pageNum,
                                   @RequestParam(defaultValue = "10")Integer pageSize,
                                   Integer status){
        TableDataInfo<Reservation> tableDataInfo = reservationService.selectByPage(pageNum,pageSize,status);
        return AjaxResult.success(tableDataInfo);
    }

    @PutMapping("/{id}/cancel")
    public AjaxResult cancel(@PathVariable("id") Long id){
        reservationService.cancelReservation(id);
        return AjaxResult.success();
    }
}
