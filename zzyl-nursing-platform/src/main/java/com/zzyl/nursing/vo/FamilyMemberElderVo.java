package com.zzyl.nursing.vo;

import com.zzyl.nursing.domain.FamilyMemberElder;
import lombok.Data;

@Data
public class FamilyMemberElderVo extends FamilyMemberElder {

    /**
     * 老人姓名
     */
    private String elderName;
}
