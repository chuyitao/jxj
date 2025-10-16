package com.zzyl.common.utils;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.*;

public class PDFUtil {

    public static String pdfToString(InputStream  inputStream) {

        PDDocument document = null;


        try {
            // 加载PDF文档
            document = PDDocument.load(inputStream);

            // 创建一个PDFTextStripper实例来提取文本
            PDFTextStripper pdfStripper = new PDFTextStripper();

            // 从PDF文档中提取文本
            String text = pdfStripper.getText(document);
            return text;

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭PDF文档
            if (document != null) {
                try {
                    document.close();
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static void main(String[] args) throws FileNotFoundException {
        FileInputStream in = new FileInputStream("D:\\授课\\昌平428\\中州养老\\day04-智能评估\\资料\\体检报告-张芳-女-72岁.pdf");
        String result = pdfToString(in);
        System.out.println(result);
    }
}