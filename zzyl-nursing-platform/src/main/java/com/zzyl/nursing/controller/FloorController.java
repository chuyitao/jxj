package com.zzyl.nursing.controller;

import com.zzyl.common.annotation.Log;
import com.zzyl.common.core.controller.BaseController;
import com.zzyl.common.core.domain.AjaxResult;
import com.zzyl.common.core.domain.R;
import com.zzyl.common.enums.BusinessType;
import com.zzyl.nursing.domain.Floor;
import com.zzyl.nursing.service.IFloorService;
import com.zzyl.nursing.vo.TreeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 楼层Controller
 * 
 * @author ruoyi
 * @date 2024-04-26
 */
@RestController
@RequestMapping("/elder/floor")
public class FloorController extends BaseController
{
    @Autowired
    private IFloorService floorService;

    @GetMapping("/getAllFloorsWithNur")
    public R<List<Floor>> getAllFloorsWithNur() {
        List<Floor> list = floorService.selectAllByNur();
        return R.ok(list);
    }

    @GetMapping("/getRoomAndBedByBedStatus/{status}")
    public AjaxResult getRoomAndBedByBedStatus(@PathVariable("status") Integer status){
        List<TreeVo> list = floorService.getRoomAndBedByBedStatus(status);
        return success(list);
    }

    /**
     * 查询楼层列表
     */
    @PreAuthorize("@ss.hasPermi('elder:floor:list')")
    @GetMapping("/list")
    public R<List<Floor>> list()
    {
        List<Floor> list = floorService.list();
        return R.ok(list);
    }

    /**
     * 获取楼层详细信息
     */
    @PreAuthorize("@ss.hasPermi('elder:floor:query')")
    @GetMapping(value = "/{id}")
    public R<Floor> getInfo(@PathVariable("id") Long id)
    {
        return R.ok(floorService.selectFloorById(id));
    }

    /**
     * 新增楼层
     */
    @PreAuthorize("@ss.hasPermi('elder:floor:add')")
    @Log(title = "楼层", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Floor floor)
    {
        return toAjax(floorService.insertFloor(floor));
    }

    /**
     * 修改楼层
     */
    @PreAuthorize("@ss.hasPermi('elder:floor:edit')")
    @Log(title = "楼层", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Floor floor)
    {
        return toAjax(floorService.updateFloor(floor));
    }

    /**
     * 删除楼层
     */
    @PreAuthorize("@ss.hasPermi('elder:floor:remove')")
    @Log(title = "楼层", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(floorService.deleteFloorByIds(ids));
    }
}
