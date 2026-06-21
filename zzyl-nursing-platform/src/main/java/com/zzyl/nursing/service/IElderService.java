package com.zzyl.nursing.service;

import java.util.List;
import com.zzyl.nursing.domain.Elder;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 老人Service接口
 * 
 * @author ruoyi
 * @date 2025-08-24
 */
public interface IElderService extends IService<Elder>
{
    /**
     * 查询老人
     * 
     * @param id 老人主键
     * @return 老人
     */
    public Elder selectElderById(Long id);

    /**
     * 查询老人列表
     * 
     * @param elder 老人
     * @return 老人集合
     */
    public List<Elder> selectElderList(Elder elder);

    /**
     * 新增老人
     * 
     * @param elder 老人
     * @return 结果
     */
    public int insertElder(Elder elder);

    /**
     * 修改老人
     * 
     * @param elder 老人
     * @return 结果
     */
    public int updateElder(Elder elder);

    /**
     * 批量删除老人
     * 
     * @param ids 需要删除的老人主键集合
     * @return 结果
     */
    public int deleteElderByIds(Long[] ids);

    /**
     * 删除老人信息
     * 
     * @param id 老人主键
     * @return 结果
     */
    public int deleteElderById(Long id);

    /**
     * 根据老人姓名或ID查询基本信息（Dify）
     *
     * @param nameOrId 老人姓名或ID
     * @return JSON 字符串
     */
    String getElderBasicInfo(String nameOrId);

    /**
     * 根据老人姓名或ID查找老人
     *
     * @param nameOrId 老人姓名或ID
     * @return 老人信息
     */
    Elder findElderByNameOrId(String nameOrId);
}
