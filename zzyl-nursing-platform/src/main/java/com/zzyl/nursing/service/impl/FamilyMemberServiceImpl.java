package com.zzyl.nursing.service.impl;

import java.util.*;

import cn.hutool.core.collection.ListUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.zzyl.common.exception.base.BaseException;
import com.zzyl.common.utils.DateUtils;
import com.zzyl.common.utils.StringUtils;
import com.zzyl.framework.web.service.TokenService;
import com.zzyl.nursing.dto.UserLoginRequestDto;
import com.zzyl.nursing.service.WechatService;
import com.zzyl.nursing.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.zzyl.nursing.mapper.FamilyMemberMapper;
import com.zzyl.nursing.domain.FamilyMember;
import com.zzyl.nursing.service.IFamilyMemberService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * 老人家属Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-08-26
 */
@Service
public class FamilyMemberServiceImpl extends ServiceImpl<FamilyMemberMapper,FamilyMember> implements IFamilyMemberService
{
    @Autowired
    private FamilyMemberMapper familyMemberMapper;

    @Autowired
    private WechatService wechatService;

    @Autowired
    private TokenService tokenService;

    static List<String> DEFAULT_NICKNAME_PREFIX = ListUtil.of("生活更美好",
            "大桔大利",
            "日富一日",
            "好柿开花",
            "柿柿如意",
            "一椰暴富",
            "大柚所为",
            "杨梅吐气",
            "天生荔枝"
    );

    /**
     * 登录
     *
     * @param userLoginRequestDto
     * @return
     */
    @Override
    public LoginVo login(UserLoginRequestDto userLoginRequestDto) {

        try {
            //获取openid
            String openid = wechatService.getOpenid(userLoginRequestDto.getCode());

            //查询用户
            FamilyMember familyMember = getOne(Wrappers.<FamilyMember>lambdaQuery().eq(FamilyMember::getOpenId, openid));

            //获取手机号
            String phone = wechatService.getPhone(userLoginRequestDto.getPhoneCode());

            //保存更新用户
            if(null == familyMember){
                //组装名称  随机字符串+手机号后4位
                Random random = new Random();
                int index = random.nextInt(DEFAULT_NICKNAME_PREFIX.size());
                String name = DEFAULT_NICKNAME_PREFIX.get( index) + phone.substring(phone.length()-4);

                familyMember = FamilyMember.builder()
                        .openId(openid)
                        .phone( phone)
                        .name(name)
                        .build();
                save(familyMember);
            }else if(null != familyMember && !familyMember.getPhone().equals(phone)) {
                familyMember.setPhone( phone);
                updateById(familyMember);
            }

            //生成token返回
            LoginVo loginVo = new LoginVo();
            Map<String,Object> claims = new HashMap<>();
            claims.put("userId",familyMember.getId());
            claims.put("name",familyMember.getName());
            loginVo.setToken(tokenService.createToken(claims));
            loginVo.setNickName(familyMember.getName());
            return loginVo;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException("登录失败");
        }
    }

    /**
     * 查询老人家属
     * 
     * @param id 老人家属主键
     * @return 老人家属
     */
    @Override
    public FamilyMember selectFamilyMemberById(Long id)
    {
        return getById(id);
    }

    /**
     * 查询老人家属列表
     * 
     * @param familyMember 老人家属
     * @return 老人家属
     */
    @Override
    public List<FamilyMember> selectFamilyMemberList(FamilyMember familyMember)
    {
        return familyMemberMapper.selectFamilyMemberList(familyMember);
    }

    /**
     * 新增老人家属
     * 
     * @param familyMember 老人家属
     * @return 结果
     */
    @Override
    public int insertFamilyMember(FamilyMember familyMember)
    {
        return save(familyMember)?1:0;
    }

    /**
     * 修改老人家属
     * 
     * @param familyMember 老人家属
     * @return 结果
     */
    @Override
    public int updateFamilyMember(FamilyMember familyMember)
    {
        return updateById(familyMember)?1:0;
    }

    /**
     * 批量删除老人家属
     * 
     * @param ids 需要删除的老人家属主键
     * @return 结果
     */
    @Override
    public int deleteFamilyMemberByIds(Long[] ids)
    {
        return removeByIds(Arrays.asList(ids))?1:0;
    }

    /**
     * 删除老人家属信息
     * 
     * @param id 老人家属主键
     * @return 结果
     */
    @Override
    public int deleteFamilyMemberById(Long id)
    {
        return removeById(id)?1:0;
    }

}
