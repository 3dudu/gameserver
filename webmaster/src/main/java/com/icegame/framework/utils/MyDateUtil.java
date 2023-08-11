package com.icegame.framework.utils;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.NumberUtil;

import java.util.Date;

/**
 * @author wuzhijian
 */
public class MyDateUtil extends cn.hutool.core.date.DateUtil {

    public static final String ISO8601_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";

    /**
     * 当前日期是否在日期指定范围内<br>
     * @param date 当前日期字符串
     * @param beginDate 起始日期字符串
     * @param endDate 截止日期字符串
     * @return 是否在范围内
     */
    public static boolean isIn(String date, String beginDate, String endDate) {
        return isIn(parse(date),parse(beginDate),parse(endDate));
    }

    /**
     * @param string 源字符串
     * @return 是否为时间格式
     */
    public static boolean isDate(String string){
        try {
            DateTime date;
            if(NumberUtil.isNumber(string)){
                date = date(Long.parseLong(string));
            } else {
                date = parse(string);
            }
            System.out.println("MyDateUtil.isDate(" + string + "): " + date);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 时间戳转化为日期字符串
     * @param timestamp long类型的时间戳 1607529600000 带毫秒数
     * @return 格式化之后的日期字符串 yyyy-MM-dd HH:mm:ss
     */
    public static String timestamp2DateString(long timestamp) {
        return date(timestamp).toString();
    }

    /**
     * 时间戳转化为日期字符串
     * @param timestamp 字符串类型的 1607529600000 带毫秒数
     * @return 格式化之后的日期字符串 yyyy-MM-dd HH:mm:ss
     */
    public static String timestamp2DateString(String timestamp) {
        return date(Long.parseLong(timestamp)).toString();
    }

    /**
     * 日期字符串转化为时间戳格式
     * @param dateString 日期字符串 yyyy-MM-dd HH:mm:ss
     * @return 转化之后的时间戳 1607529600000 带毫秒数
     */
    public static long dateString2Timestamp(String dateString) {
        return parse(dateString).getTime();
    }

    /**
     * 计算开服天数
     * @param dateString 开服时间 日期字符串 yyyy-MM-dd HH:mm:ss
     * @return 天数
     */
    public static long serverOpenDays(String dateString){
        // 开服天数不满足1天向上取整
        long l = dateString2Timestamp(dateString);
        long val = System.currentTimeMillis() - l;
        if (val <= 86400 * 1000){
            return 1;
        }
        Date date1 = parse(dateString);
        Date date2 = new Date();
        return betweenDay(date1,date2,false);
    }

}
