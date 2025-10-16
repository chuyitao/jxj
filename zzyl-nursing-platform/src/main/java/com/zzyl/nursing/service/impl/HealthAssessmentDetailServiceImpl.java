package com.zzyl.nursing.service.impl;

import java.util.List;
import com.zzyl.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.zzyl.nursing.mapper.HealthAssessmentDetailMapper;
import com.zzyl.nursing.domain.HealthAssessmentDetail;
import com.zzyl.nursing.service.IHealthAssessmentDetailService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.Arrays;
/**
 * 健康评估详情Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-08-22
 */
@Service
public class HealthAssessmentDetailServiceImpl extends ServiceImpl<HealthAssessmentDetailMapper,HealthAssessmentDetail> implements IHealthAssessmentDetailService
{
    @Autowired
    private HealthAssessmentDetailMapper healthAssessmentDetailMapper;

    /**
     * 查询健康评估详情
     * 
     * @param id 健康评估详情主键
     * @return 健康评估详情
     */
    @Override
    public HealthAssessmentDetail selectHealthAssessmentDetailById(Long id)
    {
        return getById(id);
    }

    /**
     * 查询健康评估详情列表
     * 
     * @param healthAssessmentDetail 健康评估详情
     * @return 健康评估详情
     */
    @Override
    public List<HealthAssessmentDetail> selectHealthAssessmentDetailList(HealthAssessmentDetail healthAssessmentDetail)
    {
        return healthAssessmentDetailMapper.selectHealthAssessmentDetailList(healthAssessmentDetail);
    }

    /**
     * 新增健康评估详情
     * 
     * @param healthAssessmentDetail 健康评估详情
     * @return 结果
     */
    @Override
    public int insertHealthAssessmentDetail(HealthAssessmentDetail healthAssessmentDetail)
    {
        return save(healthAssessmentDetail)?1:0;
    }

    /**
     * 修改健康评估详情
     * 
     * @param healthAssessmentDetail 健康评估详情
     * @return 结果
     */
    @Override
    public int updateHealthAssessmentDetail(HealthAssessmentDetail healthAssessmentDetail)
    {
        return updateById(healthAssessmentDetail)?1:0;
    }

    /**
     * 批量删除健康评估详情
     * 
     * @param ids 需要删除的健康评估详情主键
     * @return 结果
     */
    @Override
    public int deleteHealthAssessmentDetailByIds(Long[] ids)
    {
        return removeByIds(Arrays.asList(ids))?1:0;
    }

    /**
     * 删除健康评估详情信息
     * 
     * @param id 健康评估详情主键
     * @return 结果
     */
    @Override
    public int deleteHealthAssessmentDetailById(Long id)
    {
        return removeById(id)?1:0;
    }

}
