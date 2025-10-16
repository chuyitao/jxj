package com.zzyl.nursing.controller;

import com.zzyl.common.annotation.Log;
import com.zzyl.common.core.controller.BaseController;
import com.zzyl.common.core.domain.AjaxResult;
import com.zzyl.common.core.page.TableDataInfo;
import com.zzyl.common.enums.BusinessType;
import com.zzyl.nursing.domain.Reservation;
import com.zzyl.nursing.dto.ReservationQueryDto;
import com.zzyl.nursing.service.IReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 预约信息Controller
 * 
 * @author ruoyi
 * @date 2025-07-25
 */
@RestController
@RequestMapping("/nursing/reservation")
public class ReservationController extends BaseController
{
    @Autowired
    private IReservationService reservationService;

    /**
     * 查询预约信息列表
     */
    @PreAuthorize("@ss.hasPermi('nursing:reservation:list')")
    @GetMapping("/list")
    public TableDataInfo list(ReservationQueryDto reservation)
    {
        startPage();
        List<Reservation> list = reservationService.selectReservationList(reservation);
        return getDataTable(list);
    }


    /**
     * 获取预约信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('nursing:reservation:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(reservationService.selectReservationById(id));
    }

    /**
     * 修改预约信息
     */
    @PreAuthorize("@ss.hasPermi('nursing:reservation:edit')")
    @Log(title = "预约信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Reservation reservation)
    {
        return toAjax(reservationService.updateReservation(reservation));
    }

    /**
     * 删除预约信息
     */
    @PreAuthorize("@ss.hasPermi('nursing:reservation:remove')")
    @Log(title = "预约信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(reservationService.deleteReservationByIds(ids));
    }
}
