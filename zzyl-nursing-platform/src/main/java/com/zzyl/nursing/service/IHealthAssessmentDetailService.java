package com.zzyl.nursing.service;

import java.util.List;
import com.zzyl.nursing.domain.HealthAssessmentDetail;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 健康评估详情Service接口
 * 
 * @author ruoyi
 * @date 2025-08-22
 */
public interface IHealthAssessmentDetailService extends IService<HealthAssessmentDetail>
{
    /**
     * 查询健康评估详情
     * 
     * @param id 健康评估详情主键
     * @return 健康评估详情
     */
    public HealthAssessmentDetail selectHealthAssessmentDetailById(Long id);

    /**
     * 查询健康评估详情列表
     * 
     * @param healthAssessmentDetail 健康评估详情
     * @return 健康评估详情集合
     */
    public List<HealthAssessmentDetail> selectHealthAssessmentDetailList(HealthAssessmentDetail healthAssessmentDetail);

    /**
     * 新增健康评估详情
     * 
     * @param healthAssessmentDetail 健康评估详情
     * @return 结果
     */
    public int insertHealthAssessmentDetail(HealthAssessmentDetail healthAssessmentDetail);

    /**
     * 修改健康评估详情
     * 
     * @param healthAssessmentDetail 健康评估详情
     * @return 结果
     */
    public int updateHealthAssessmentDetail(HealthAssessmentDetail healthAssessmentDetail);

    /**
     * 批量删除健康评估详情
     * 
     * @param ids 需要删除的健康评估详情主键集合
     * @return 结果
     */
    public int deleteHealthAssessmentDetailByIds(Long[] ids);

    /**
     * 删除健康评估详情信息
     * 
     * @param id 健康评估详情主键
     * @return 结果
     */
    public int deleteHealthAssessmentDetailById(Long id);
}
