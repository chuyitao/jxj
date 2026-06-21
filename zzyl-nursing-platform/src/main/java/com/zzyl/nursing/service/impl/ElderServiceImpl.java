package com.zzyl.nursing.service.impl;

import java.util.List;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.zzyl.common.utils.IdCardUtils;
import com.zzyl.common.utils.StringUtils;
import com.zzyl.nursing.domain.Bed;
import com.zzyl.nursing.domain.CheckIn;
import com.zzyl.nursing.domain.Room;
import com.zzyl.nursing.mapper.BedMapper;
import com.zzyl.nursing.mapper.CheckInMapper;
import com.zzyl.nursing.mapper.ElderMapper;
import com.zzyl.nursing.mapper.RoomMapper;
import com.zzyl.nursing.vo.ElderBasicInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.zzyl.nursing.domain.Elder;
import com.zzyl.nursing.service.IElderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.Arrays;
/**
 * 老人Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-08-24
 */
@Service
public class ElderServiceImpl extends ServiceImpl<ElderMapper,Elder> implements IElderService
{
    @Autowired
    private ElderMapper elderMapper;

    @Autowired
    private CheckInMapper checkInMapper;

    @Autowired
    private BedMapper bedMapper;

    @Autowired
    private RoomMapper roomMapper;

    /**
     * 查询老人
     * 
     * @param id 老人主键
     * @return 老人
     */
    @Override
    public Elder selectElderById(Long id)
    {
        return getById(id);
    }

    /**
     * 查询老人列表
     * 
     * @param elder 老人
     * @return 老人
     */
    @Override
    public List<Elder> selectElderList(Elder elder)
    {
        return elderMapper.selectElderList(elder);
    }

    /**
     * 新增老人
     * 
     * @param elder 老人
     * @return 结果
     */
    @Override
    public int insertElder(Elder elder)
    {
        return save(elder)?1:0;
    }

    /**
     * 修改老人
     * 
     * @param elder 老人
     * @return 结果
     */
    @Override
    public int updateElder(Elder elder)
    {
        return updateById(elder)?1:0;
    }

    /**
     * 批量删除老人
     * 
     * @param ids 需要删除的老人主键
     * @return 结果
     */
    @Override
    public int deleteElderByIds(Long[] ids)
    {
        return removeByIds(Arrays.asList(ids))?1:0;
    }

    /**
     * 删除老人信息
     * 
     * @param id 老人主键
     * @return 结果
     */
    @Override
    public int deleteElderById(Long id)
    {
        return removeById(id)?1:0;
    }

    @Override
    public String getElderBasicInfo(String nameOrId)
    {
        if (StringUtils.isEmpty(nameOrId))
        {
            return null;
        }

        Elder elder = findElderByNameOrId(nameOrId);
        if (ObjectUtil.isEmpty(elder))
        {
            return null;
        }

        ElderBasicInfoVO vo = new ElderBasicInfoVO();
        vo.setElderId(elder.getId());
        vo.setName(elder.getName());
        vo.setPhone(elder.getPhone());
        vo.setAddress(elder.getAddress());
        vo.setSex(elder.getSex());
        vo.setSexName(formatSex(elder.getSex()));
        vo.setAge(calculateAge(elder));
        vo.setBedNumber(elder.getBedNumber());

        CheckIn checkIn = checkInMapper.selectOne(Wrappers.<CheckIn>lambdaQuery()
                .eq(CheckIn::getElderId, elder.getId())
                .eq(CheckIn::getStatus, 0)
                .orderByDesc(CheckIn::getStartDate)
                .last("limit 1"));
        if (ObjectUtil.isNotEmpty(checkIn))
        {
            if (checkIn.getStartDate() != null)
            {
                vo.setCheckInTime(checkIn.getStartDate().toString());
            }
            vo.setNursingLevelName(checkIn.getNursingLevelName());
            if (StringUtils.isEmpty(vo.getBedNumber()))
            {
                vo.setBedNumber(checkIn.getBedNumber());
            }
        }

        vo.setRoomNumber(resolveRoomNumber(elder));
        return JSONUtil.toJsonStr(vo);
    }

    @Override
    public Elder findElderByNameOrId(String keyword)
    {
        keyword = keyword.trim();
        if (keyword.matches("\\d+"))
        {
            return getById(Long.parseLong(keyword));
        }
        Elder elder = getOne(Wrappers.<Elder>lambdaQuery()
                .eq(Elder::getName, keyword)
                .last("limit 1"));
        if (ObjectUtil.isNotEmpty(elder))
        {
            return elder;
        }
        return getOne(Wrappers.<Elder>lambdaQuery()
                .like(Elder::getName, keyword)
                .last("limit 1"));
    }

    private Integer calculateAge(Elder elder)
    {
        if (StringUtils.isNotEmpty(elder.getIdCardNo()))
        {
            try
            {
                return IdCardUtils.getAge(elder.getIdCardNo());
            }
            catch (IllegalArgumentException ignored)
            {
            }
        }
        return null;
    }

    private String formatSex(Integer sex)
    {
        if (sex == null)
        {
            return "未知";
        }
        return sex == 0 ? "女" : "男";
    }

    private String resolveRoomNumber(Elder elder)
    {
        if (elder.getBedId() == null)
        {
            return null;
        }
        Bed bed = bedMapper.selectById(elder.getBedId());
        if (ObjectUtil.isEmpty(bed) || bed.getRoomId() == null)
        {
            return null;
        }
        Room room = roomMapper.selectById(bed.getRoomId());
        return ObjectUtil.isNotEmpty(room) ? room.getCode() : null;
    }

}
