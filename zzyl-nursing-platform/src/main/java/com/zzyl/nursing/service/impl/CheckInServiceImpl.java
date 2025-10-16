package com.zzyl.nursing.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.zzyl.common.exception.base.BaseException;
import com.zzyl.common.utils.CodeGenerator;
import com.zzyl.common.utils.DateUtils;
import com.zzyl.common.utils.IdCardUtils;
import com.zzyl.nursing.domain.*;
import com.zzyl.nursing.dto.CheckInApplyDto;
import com.zzyl.nursing.dto.CheckInElderDto;
import com.zzyl.nursing.mapper.*;
import com.zzyl.nursing.service.IHealthAssessmentService;
import com.zzyl.nursing.vo.CheckInConfigVo;
import com.zzyl.nursing.vo.CheckInDetailVo;
import com.zzyl.nursing.vo.CheckInElderVo;
import com.zzyl.nursing.vo.ElderFamilyVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.zzyl.nursing.service.ICheckInService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

/**
 * 入住Service业务层处理
 *
 * @author ruoyi
 * @date 2025-08-24
 */
@Service
public class CheckInServiceImpl extends ServiceImpl<CheckInMapper, CheckIn> implements ICheckInService {
    @Autowired
    private CheckInMapper checkInMapper;

    @Autowired
    private ElderMapper elderMapper;

    @Autowired
    private BedMapper bedMapper;

    @Autowired
    private IHealthAssessmentService healthAssessmentService;

    /**
     * 详情
     * @param id
     * @return
     */
    @Override
    public CheckInDetailVo detail(Long id) {
        CheckInDetailVo vo = new CheckInDetailVo();
        //老人的基本信息    入住配置  合同  家属列表
        CheckIn checkIn = getById(id);
        Long elderId = checkIn.getElderId();
        Elder elder = elderMapper.selectElderById(elderId);
        if(ObjectUtil.isNotEmpty(elder)){
            //属性拷贝
            CheckInElderVo checkInElderVo = BeanUtil.toBean(elder, CheckInElderVo.class);
            checkInElderVo.setAge(IdCardUtils.getAge(elder.getIdCardNo()));
            vo.setCheckInElderVo(checkInElderVo);
        }
        //入住配置
        CheckInConfig checkInConfig = checkInConfigMapper.selectOne(Wrappers.<CheckInConfig>lambdaQuery().eq(CheckInConfig::getCheckInId, id));
        //属性拷贝
        CheckInConfigVo checkInConfigVo = BeanUtil.toBean(checkInConfig, CheckInConfigVo.class);
        checkInConfigVo.setBedNumber(checkIn.getBedNumber());
        checkInConfigVo.setStartDate(checkIn.getStartDate());
        checkInConfigVo.setEndDate(checkIn.getEndDate());
        vo.setCheckInConfigVo(checkInConfigVo);
        //合同
        Contract contract = contractMapper.selectOne(Wrappers.<Contract>lambdaQuery().eq(Contract::getElderId, elder.getId()));
        vo.setContract(contract);

        //家属列表
        String remark = checkIn.getRemark();
        List<ElderFamilyVo> list = JSONUtil.toList(remark, ElderFamilyVo.class);
        vo.setElderFamilyVoList(list);

        return vo;
    }

    /**
     * 申请入住
     * <p>
     * 老人是否是之前入住过
     * 保存老人  入住表，入住配置表  合同表
     * 修改 床位的状态，修改智能评估的状态
     *
     * @param dto
     */
    @Transactional
    @Override
    public void apply(CheckInApplyDto dto) {
        try {
            //根据身份证号和状态
            //select * from elder where id_card = 1111 and status = 1
            Elder elder = elderMapper.selectOne(Wrappers.<Elder>lambdaQuery()
                    .eq(Elder::getIdCardNo, dto.getCheckInElderDto().getIdCardNo())
                    .eq(Elder::getStatus, 1));
            if (null != elder) {
                throw new BaseException("老人已入住");
            }

            //更新床位状态
            Bed bed = bedMapper.selectById(dto.getCheckInConfigDto().getBedId());
            bed.setBedStatus(1);
            bedMapper.updateById(bed);

            //新增或 更新老人
            elder = insertOrUpdateElder(dto.getCheckInElderDto(), bed);

            //新增签约办理  合同编号
            insertContract(dto, elder);

            //新增入住信息
            CheckIn checkIn = insertCheckIn(dto, elder);

            //新增入住配置信息
            insertCheckInConfig(dto,checkIn);

            //修改智能评估状态
            // update health_assessment set admission_status = 0 where id_card = 1111  and admission_status = 1
            LambdaUpdateWrapper<HealthAssessment> updateWrapper = new LambdaUpdateWrapper();
            updateWrapper.eq(HealthAssessment::getIdCard,elder.getIdCardNo());
            updateWrapper.eq(HealthAssessment::getAdmissionStatus,1);
            updateWrapper.set(HealthAssessment::getAdmissionStatus,0);
            healthAssessmentService.update(updateWrapper);
        } catch (BaseException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    @Autowired
    private CheckInConfigMapper checkInConfigMapper;

    /**
     * 入住配置
     * @param dto
     * @param checkIn
     */
    private void insertCheckInConfig(CheckInApplyDto dto, CheckIn checkIn) {

        //属性拷贝
        CheckInConfig checkInConfig = BeanUtil.toBean(dto.getCheckInConfigDto(), CheckInConfig.class);
        checkInConfig.setCheckInId(checkIn.getId());
        checkInConfigMapper.insert(checkInConfig);
    }

    /**
     * 新增入住信息
     * @param dto
     * @param elder
     */
    private CheckIn insertCheckIn(CheckInApplyDto dto, Elder elder) {
        CheckIn checkIn = new CheckIn();
        checkIn.setStatus(0);
        checkIn.setBedNumber(elder.getBedNumber());
        checkIn.setElderId(elder.getId());
        checkIn.setElderName(elder.getName());
        checkIn.setIdCardNo(elder.getIdCardNo());
        checkIn.setStartDate(dto.getCheckInConfigDto().getStartDate());
        checkIn.setEndDate(dto.getCheckInConfigDto().getEndDate());
        checkIn.setNursingLevelName(dto.getCheckInConfigDto().getNursingLevelName());
        //把家属的信息，转换为json字符串，存储remark中
        String jsonStr = JSONUtil.toJsonStr(dto.getElderFamilyDtoList());
        checkIn.setRemark(jsonStr);

        save(checkIn);
        return checkIn;
    }

    @Autowired
    private ContractMapper contractMapper;

    /**
     * 新增签约办理
     *
     * @param dto
     * @param elder
     */
    private void insertContract(CheckInApplyDto dto, Elder elder) {
        //属性拷贝
        Contract contract = BeanUtil.toBean(dto.getCheckInContractDto(), Contract.class);
        //老人相关的信息
        contract.setElderId(elder.getId());
        contract.setElderName(elder.getName());

        //合同编号
        contract.setContractNumber("HT"+CodeGenerator.generateContractNumber());

        //入住配置中的开始和结束时间
        LocalDateTime startDate = dto.getCheckInConfigDto().getStartDate();
        LocalDateTime endDate = dto.getCheckInConfigDto().getEndDate();
        contract.setStartDate(startDate);
        contract.setEndDate(endDate);
        //状态判断
        Integer status = startDate.isAfter(LocalDateTime.now()) ? 0 : 1;
        contract.setStatus(status);
        contractMapper.insert(contract);
    }

    /**
     * 新增或更新老人
     *
     * @param checkInElderDto
     * @param bed
     */
    private Elder insertOrUpdateElder(CheckInElderDto checkInElderDto, Bed bed) {
        //属性拷贝
        Elder elder = BeanUtil.toBean(checkInElderDto, Elder.class);
        elder.setBedId(bed.getId());
        elder.setBedNumber(bed.getBedNumber());
        elder.setStatus(1);

        Elder elderDb = elderMapper.selectOne(Wrappers.<Elder>lambdaQuery()
                .eq(Elder::getIdCardNo, checkInElderDto.getIdCardNo())
                .eq(Elder::getStatus, 0));
        if (elderDb != null) {
            //更新
            elder.setId(elderDb.getId());
            elderMapper.updateById(elder);
        } else {
            //新增
            elderMapper.insert(elder);
        }
        return elder;

    }

    /**
     * 查询入住
     *
     * @param id 入住主键
     * @return 入住
     */
    @Override
    public CheckIn selectCheckInById(Long id) {
        return getById(id);
    }

    /**
     * 查询入住列表
     *
     * @param checkIn 入住
     * @return 入住
     */
    @Override
    public List<CheckIn> selectCheckInList(CheckIn checkIn) {
        return checkInMapper.selectCheckInList(checkIn);
    }

    /**
     * 新增入住
     *
     * @param checkIn 入住
     * @return 结果
     */
    @Override
    public int insertCheckIn(CheckIn checkIn) {
        return save(checkIn) ? 1 : 0;
    }

    /**
     * 修改入住
     *
     * @param checkIn 入住
     * @return 结果
     */
    @Override
    public int updateCheckIn(CheckIn checkIn) {
        return updateById(checkIn) ? 1 : 0;
    }

    /**
     * 批量删除入住
     *
     * @param ids 需要删除的入住主键
     * @return 结果
     */
    @Override
    public int deleteCheckInByIds(Long[] ids) {
        return removeByIds(Arrays.asList(ids)) ? 1 : 0;
    }

    /**
     * 删除入住信息
     *
     * @param id 入住主键
     * @return 结果
     */
    @Override
    public int deleteCheckInById(Long id) {
        return removeById(id) ? 1 : 0;
    }

}
