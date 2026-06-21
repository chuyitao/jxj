package com.zzyl.oss.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.CreateBucketRequest;
import com.aliyun.oss.model.SetBucketLoggingRequest;
import com.zzyl.oss.properties.AliOssConfigProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class OssAliyunAutoConfig {

    @Autowired
    private AliOssConfigProperties aliOssConfigProperties;

    @Bean
    public OSS ossClient(){
        log.info("-----------------开始创建OSSClient--------------------");
        OSS ossClient = new OSSClientBuilder().build(aliOssConfigProperties.getEndpoint(),
                aliOssConfigProperties.getAccessKeyId(), aliOssConfigProperties.getAccessKeySecret());
        String bucketName = aliOssConfigProperties.getBucketName();
        try {
            if (!ossClient.doesBucketExist(bucketName)) {
                ossClient.createBucket(bucketName);
                CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucketName);
                createBucketRequest.setCannedACL(CannedAccessControlList.PublicRead);
                ossClient.createBucket(createBucketRequest);
            }

            SetBucketLoggingRequest request = new SetBucketLoggingRequest(bucketName);
            request.setTargetBucket(bucketName);
            request.setTargetPrefix(bucketName);
            ossClient.setBucketLogging(request);
        } catch (Exception e) {
            log.warn("OSS 初始化非关键步骤失败，不影响启动: {}", e.getMessage());
        }

        log.info("-----------------结束创建OSSClient--------------------");
        return ossClient;
    }


}
