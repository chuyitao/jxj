package com.zzyl.nursing.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.zzyl.common.exception.base.BaseException;
import com.zzyl.common.utils.DateUtils;
import com.zzyl.common.utils.IdCardUtils;
import com.zzyl.common.utils.StringUtils;
import com.zzyl.common.utils.deepseek.ChatClient;
import com.zzyl.framework.config.properties.DeepseekProperties;
import com.zzyl.nursing.domain.HealthAssessmentDetail;
import com.zzyl.nursing.domain.Elder;
import com.zzyl.nursing.dto.HealthAssessmentDto;
import com.zzyl.nursing.mapper.HealthAssessmentDetailMapper;
import com.zzyl.nursing.vo.HealthAssessmentVo;
import com.zzyl.nursing.vo.health.HealthReportVo;
import org.aspectj.weaver.ast.Var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.zzyl.nursing.mapper.HealthAssessmentMapper;
import com.zzyl.nursing.domain.HealthAssessment;
import com.zzyl.nursing.service.IHealthAssessmentService;
import com.zzyl.nursing.service.IElderService;
import com.zzyl.nursing.vo.ElderHealthInfoVO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

/**
 * 健康评估Service业务层处理
 *
 * @author ruoyi
 * @date 2025-08-22
 */
@Service
public class HealthAssessmentServiceImpl extends ServiceImpl<HealthAssessmentMapper, HealthAssessment> implements IHealthAssessmentService {
    @Autowired
    private HealthAssessmentMapper healthAssessmentMapper;

    @Autowired
    private IElderService elderService;

    /**
     * 查询健康评估
     *
     * @param id 健康评估主键
     * @return 健康评估
     */
    @Override
    public HealthAssessmentVo selectHealthAssessmentById(Long id) {
        //主表
        HealthAssessment healthAssessment = getById(id);

        /*LambdaQueryWrapper<HealthAssessmentDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(HealthAssessmentDetail::getHealthAssessmentId, id);*/
        //详情表
        HealthAssessmentDetail healthAssessmentDetail = healthAssessmentDetailMapper
                .selectOne(Wrappers
                        .<HealthAssessmentDetail>lambdaQuery()
                .eq(HealthAssessmentDetail::getHealthAssessmentId, id));

        //合并
        HealthAssessmentVo healthAssessmentVo = BeanUtil.toBean(healthAssessment, HealthAssessmentVo.class);
        //拷贝
        BeanUtil.copyProperties(healthAssessmentDetail, healthAssessmentVo);

        return healthAssessmentVo;
    }

    /**
     * 查询健康评估列表
     *
     * @param healthAssessment 健康评估
     * @return 健康评估
     */
    @Override
    public List<HealthAssessment> selectHealthAssessmentList(HealthAssessment healthAssessment) {
        return healthAssessmentMapper.selectHealthAssessmentList(healthAssessment);
    }

    @Autowired
    private DeepseekProperties deepseekProperties;

    /**
     * 新增健康评估
     *
     * @param healthAssessmentDto 健康评估
     * @return 结果
     */
    @Transactional
    @Override
    public Long insertHealthAssessment(HealthAssessmentDto healthAssessmentDto) {
        try {
            //组装提示词
            String prompt = getPrompt(healthAssessmentDto.getIdCard());

            //调用ai分析
            ChatClient chatClient =
                    new ChatClient(deepseekProperties.getBaseUrl(),
                            deepseekProperties.getApiKey(),
                            deepseekProperties.getModel(), "");
            String result = chatClient.chat(prompt);

            //解析结果
            result = result.startsWith("```json") ? result
                    .replace("```json", "")
                    .replace("```", "") : result;

            System.out.println(result);
            //把json数据封装为对象
            HealthReportVo healthReportVo = JSONUtil.toBean(result, HealthReportVo.class);

            //保存 health_assessment  health_assessment_detail
            Long id = insertHealthAssessment(healthReportVo, healthAssessmentDto);

            return id;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException("AI分析失败");
        }

    }

    @Autowired
    private HealthAssessmentDetailMapper healthAssessmentDetailMapper;

    /**
     * 插入健康评估
     *
     * @param healthReportVo
     * @param healthAssessmentDto
     * @return
     */
    private Long insertHealthAssessment(HealthReportVo healthReportVo, HealthAssessmentDto healthAssessmentDto) {

        HealthAssessment healthAssessment = new HealthAssessment();
        //前端传的  老人姓名
        String idCard = healthAssessmentDto.getIdCard();
        healthAssessment.setElderName(healthAssessmentDto.getElderName());
        healthAssessment.setIdCard(idCard);
        //ai分析之后的分数
        double healthIndex = healthReportVo.getHealthIndex();
        healthAssessment.setHealthScore(healthIndex + "");
        healthAssessment.setSuggestionForAdmission(healthIndex > 60 ? 0 : 1);
        healthAssessment.setNursingLevelName(getNursingLevelName(healthIndex));
        healthAssessment.setAdmissionStatus(1);
        healthAssessment.setTotalCheckDate(healthReportVo.getTotalCheckDate());
        healthAssessment.setAssessmentTime(LocalDateTime.now());
        //---------------------------------

        healthAssessmentMapper.insertHealthAssessment(healthAssessment);

        Long id = healthAssessment.getId();

        HealthAssessmentDetail detail = new HealthAssessmentDetail();
        detail.setHealthAssessmentId(id);
        detail.setBirthDate(IdCardUtils.getBirthDate(idCard));
        detail.setAge(IdCardUtils.getAge(idCard));
        detail.setGender(IdCardUtils.getGender(idCard));
        detail.setRiskLevel(healthReportVo.getRiskLevel());
        detail.setPhysicalExamInstitution(healthAssessmentDto.getPhysicalExamInstitution());
        detail.setPhysicalReportUrl(healthAssessmentDto.getPhysicalReportUrl());
        detail.setReportSummary(healthReportVo.getSummarize());
        detail.setAbnormalAnalysis(JSONUtil.toJsonStr(healthReportVo.getAbnormalData()));
        detail.setSystemScore(JSONUtil.toJsonStr(healthReportVo.getSystemScore()));
        healthAssessmentDetailMapper.insertHealthAssessmentDetail(detail);

        return id;
    }

    /**
     * 获取护理等级名称
     * @param healthIndex
     * @return
     */
    private String getNursingLevelName(double healthIndex) {
        if (healthIndex > 90) {
            return "三级护理等级";
        } else if (healthIndex > 80) {
            return "二级护理等级";
        } else if (healthIndex > 70) {
            return "一级护理等级";
        } else if (healthIndex > 60) {
            return "特级护理等级";
        } else {
            return "不建议入住";
        }
    }

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 获取提示词
     *
     * @param idCard
     * @return
     */
    private String getPrompt(String idCard) {

        String content = (String) redisTemplate.opsForHash().get("healthVoReport", idCard);
        if (StringUtils.isEmpty(content)) {
            throw new BaseException("体检内容不存在，请重新上传");
        }

        String prompt = """
                请以一个专业医生的视角来分析这份体检报告，报告中包含了一些异常数据，我需要您对这些数据进行解读，并给出相应的健康建议。
                 体检内容如下：
                 %s
                 要求：
                 1. 提取体检报告中的“总检日期”；
                 2. 通过临床医学、疾病风险评估模型和数据智能分析，给该用户的风险等级和健康指数给出结果。风险等级分为：健康、提示、风险、危险、严重危险。健康指数范围为0至100分；
                 3. 对于体检报告有异常数据，请列出（异常数据的结论、体检项目名称、检查结果、参考值、单位、异常解读、建议）这8字段。解读异常数据，解决这些数据可能代表的健康问题或风险。分析可能的原因，包括但不限于生活习惯、饮食习惯、遗传因素等。基于这些异常数据和可能的原因，请给出具体的健康建议，包括饮食调整、运动建议、生活方式改变以及是否需要进一步检查或治疗等。
                 结论格式：异常数据的结论：肥胖，体检项目名称：体重指数BMI，检查结果：29.2，参考值>24，单位：-。异常解读：体重超标包括超重与肥胖。体重指数（BMI）=体重（kg）/身⾼（m）的平⽅，BMI≥24为超重，BMI≥28为肥胖；男性腰围≥90cm和⼥性腰围≥85cm为腹型肥胖。体重超标是⼀种由多因素（如遗传、进⻝油脂较多、运动少、疾病等）引起的慢性代谢性疾病，尤其是肥胖，已经被世界卫⽣组织列为导致疾病负担的⼗⼤危险因素之⼀。AI建议：采取综合措施预防和控制体重，积极改变⽣活⽅式，宜低脂、低糖、⾼纤维素膳⻝，多⻝果蔬及菌藻类⻝物，增加有氧运动。若有相关疾病（如⾎脂异常、⾼⾎压、糖尿病等）应积极治疗。
                 4. 根据这个体检报告的内容，分别是给人体的8大系统打分，每项满分为100分，8大系统分别为：呼吸系统、消化系统、内分泌系统、免疫系统、循环系统、泌尿系统、运动系统、感官系统
                 5. 给体检报告做一个总结，总结格式：体检报告中尿蛋⽩、癌胚抗原、⾎沉、空腹⾎糖、总胆固醇、⽢油三酯、低密度脂蛋⽩胆固醇、⾎清载脂蛋⽩B、动脉硬化指数、⽩细胞、平均红细胞体积、平均⾎红蛋⽩共12项指标提示异常，尿液常规共1项指标处于临界值，⾎脂、⾎液常规、尿液常规、糖类抗原、⾎清酶类等共43项指标提示正常，综合这些临床指标和数据分析：肾脏、肝胆、⼼脑⾎管存在隐患，其中⼼脑⾎管有“⾼危”⻛险；肾脏部位有“中危”⻛险；肝胆部位有“低危”⻛险。
                               
                 输出要求：
                 最后，将以上结果输出为纯JSON格式，不要包含其他的文字说明，也不要出现Markdown语法相关的文字，所有的返回结果都是json，
                 详细格式如下，（**请严格检查json语法，不要出现丢失双引号的情况**）
                               
                 {
                   "totalCheckDate": "YYYY-MM-DD",
                   "riskLevel": "健康\\提示\\风险\\危险\\严重危险",
                   "healthIndex": XX.XX，
                   "abnormalData": [
                     {
                       "conclusion": "异常数据的结论",
                       "examinationItem": "体检项目名称",
                       "result": "检查结果",
                       "referenceValue": "参考值",
                       "unit": "单位",
                       "interpret":"对于异常的结论进一步详细的说明",
                       "advice":"针对于这一项的异常，给出一些健康的建议"
                     }
                   ],
                   "systemScore": {
                     "breathingSystem": XX,
                     "digestiveSystem": XX,
                     "endocrineSystem": XX,
                     "immuneSystem": XX,
                     "circulatorySystem": XX,
                     "urinarySystem": XX,
                     "motionSystem": XX,
                     "senseSystem": XX
                   },
                   "summarize": "体检报告的总结"
                 }
                                
                """;

        return prompt.formatted(content);

    }

    /**
     * 修改健康评估
     *
     * @param healthAssessment 健康评估
     * @return 结果
     */
    @Override
    public int updateHealthAssessment(HealthAssessment healthAssessment) {
        return updateById(healthAssessment) ? 1 : 0;
    }

    /**
     * 批量删除健康评估
     *
     * @param ids 需要删除的健康评估主键
     * @return 结果
     */
    @Override
    public int deleteHealthAssessmentByIds(Long[] ids) {
        return removeByIds(Arrays.asList(ids)) ? 1 : 0;
    }

    /**
     * 删除健康评估信息
     *
     * @param id 健康评估主键
     * @return 结果
     */
    @Override
    public int deleteHealthAssessmentById(Long id) {
        return removeById(id) ? 1 : 0;
    }

    @Override
    public String getElderHealthInfo(String nameOrId) {
        if (StringUtils.isEmpty(nameOrId)) {
            return null;
        }

        Elder elder = elderService.findElderByNameOrId(nameOrId);
        if (ObjectUtil.isEmpty(elder)) {
            return null;
        }

        HealthAssessment healthAssessment = findLatestHealthAssessment(elder);
        if (ObjectUtil.isEmpty(healthAssessment)) {
            return null;
        }

        HealthAssessmentDetail detail = healthAssessmentDetailMapper.selectOne(
                Wrappers.<HealthAssessmentDetail>lambdaQuery()
                        .eq(HealthAssessmentDetail::getHealthAssessmentId, healthAssessment.getId())
                        .last("limit 1"));

        ElderHealthInfoVO vo = new ElderHealthInfoVO();
        vo.setName(healthAssessment.getElderName());
        vo.setHealthScore(healthAssessment.getHealthScore());
        if (ObjectUtil.isNotEmpty(detail)) {
            vo.setRiskLevel(detail.getRiskLevel());
            vo.setReportSummary(detail.getReportSummary());
        }
        return JSONUtil.toJsonStr(vo);
    }

    private HealthAssessment findLatestHealthAssessment(Elder elder) {
        HealthAssessment healthAssessment = null;
        if (StringUtils.isNotEmpty(elder.getIdCardNo())) {
            healthAssessment = getOne(Wrappers.<HealthAssessment>lambdaQuery()
                    .eq(HealthAssessment::getIdCard, elder.getIdCardNo())
                    .orderByDesc(HealthAssessment::getAssessmentTime)
                    .last("limit 1"));
        }
        if (ObjectUtil.isEmpty(healthAssessment) && StringUtils.isNotEmpty(elder.getName())) {
            healthAssessment = getOne(Wrappers.<HealthAssessment>lambdaQuery()
                    .eq(HealthAssessment::getElderName, elder.getName())
                    .orderByDesc(HealthAssessment::getAssessmentTime)
                    .last("limit 1"));
        }
        return healthAssessment;
    }

}
