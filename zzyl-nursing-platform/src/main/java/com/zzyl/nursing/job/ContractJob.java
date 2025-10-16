package com.zzyl.nursing.job;

import com.zzyl.nursing.service.IContractService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ContractJob {

    @Autowired
    private IContractService contractService;

    public void updateContractStatus() {
        //更新合同状态
        contractService.updateContractStatus();
        log.info("更新合同状态成功");
    }
}
