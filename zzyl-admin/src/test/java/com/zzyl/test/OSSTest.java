package com.zzyl.test;

import com.zzyl.oss.client.OSSAliyunFileStorageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

@SpringBootTest
public class OSSTest {

    @Autowired
    private OSSAliyunFileStorageService fileStorageService;

    @Test
    public void test() throws FileNotFoundException {
        //读取一个文件
        FileInputStream in = new FileInputStream("D:\\授课\\昌平428\\中州养老\\day04-智能评估\\资料\\体检报告-张芳-女-72岁.pdf");
        //上传文件
        String url = fileStorageService.store("123.pdf", in);
        System.out.println(url);
    }
}
