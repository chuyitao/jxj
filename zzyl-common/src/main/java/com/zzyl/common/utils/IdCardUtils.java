package com.zzyl.common.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;

public class IdCardUtils {

    /**
     * 根据身份证号获取出生日期
     * @param idCard 身份证号码
     * @return 出生日期（LocalDateTime格式，时间部分为00:00:00）
     * @throws IllegalArgumentException 如果身份证号格式无效
     */
    public static LocalDateTime getBirthDate(String idCard) {
        String birthDateStr = extractBirthDateString(idCard);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        try {
            LocalDate birthDate = LocalDate.parse(birthDateStr, formatter);
            return birthDate.atStartOfDay();
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("无效的出生日期格式: " + birthDateStr);
        }
    }

    /**
     * 根据身份证号计算年龄
     * @param idCard 身份证号码
     * @return 当前年龄（整数）
     * @throws IllegalArgumentException 如果身份证号格式无效
     */
    public static Integer getAge(String idCard) {
        LocalDate birthDate = getBirthDate(idCard).toLocalDate();
        LocalDate currentDate = LocalDate.now();
        return Period.between(birthDate, currentDate).getYears();
    }

    /**
     * 根据身份证号获取性别
     * @param idCard 身份证号码
     * @return 0:男, 1:女
     * @throws IllegalArgumentException 如果身份证号格式无效
     */
    public static Integer getGender(String idCard) {
        validateIdCard(idCard);
        char genderCode;
        
        if (idCard.length() == 18) {
            genderCode = idCard.charAt(16);
        } else if (idCard.length() == 15) {
            genderCode = idCard.charAt(14);
        } else {
            throw new IllegalArgumentException("无效的身份证号长度");
        }
        
        // 奇数为男性(0)，偶数为女性(1)
        return Character.getNumericValue(genderCode) % 2 == 1 ? 0 : 1;
    }

    /**
     * 从身份证号提取出生日期字符串
     * @param idCard 身份证号码
     * @return 8位出生日期字符串（yyyyMMdd格式）
     */
    private static String extractBirthDateString(String idCard) {
        validateIdCard(idCard);
        String birthDateStr;
        
        if (idCard.length() == 18) {
            birthDateStr = idCard.substring(6, 14);
        } else {
            // 15位身份证：年份补全为19XX
            birthDateStr = "19" + idCard.substring(6, 12);
        }
        return birthDateStr;
    }

    /**
     * 验证身份证号基本格式
     * @param idCard 身份证号码
     * @throws IllegalArgumentException 如果身份证号无效
     */
    private static void validateIdCard(String idCard) {
        if (Objects.isNull(idCard)) {
            throw new IllegalArgumentException("身份证号不能为空");
        }
        
        int length = idCard.length();
        if (length != 15 && length != 18) {
            throw new IllegalArgumentException("无效的身份证号长度");
        }
        
        // 只允许18位身份证最后一位是X，其他必须是数字
        if (length == 18) {
            if (!idCard.substring(0, 17).matches("\\d+") || 
                !Character.isDigit(idCard.charAt(17)) && idCard.charAt(17) != 'X') {
                throw new IllegalArgumentException("无效的18位身份证格式");
            }
        } else {
            if (!idCard.matches("\\d+")) {
                throw new IllegalArgumentException("无效的15位身份证格式");
            }
        }
    }
}