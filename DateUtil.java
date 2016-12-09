package com.dnp.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Copyright 2016 DONOPO Ltd. All rights reserved.
 * <p>
 * Remark   : 常用日期工具类
 * <p/>
 * Author   : Tim Mars
 * Project  : Quake
 * Date     : 6/22/2016
 */
public class DateUtil {

    public static final String FORMATER_YMD = "yyyy-MM-dd";
    public static final String FORMATER_YMDHMS = "yyyy-MM-dd HH:mm:ss";


    public static Date strToDate(String _date, String format) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return sdf.parse(_date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String dateToStr(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    public static Date strToDateYmd(String _date) {
        return strToDate(_date, FORMATER_YMD);
    }

    public static String dateToStrYmd(Date date) {
        return dateToStr(date, FORMATER_YMD);
    }

    public static Date strToDateYmdhms(String _date) {
        return strToDate(_date, FORMATER_YMDHMS);
    }

    public static String dateToStrYmdhms(Date date) {
        return dateToStr(date, FORMATER_YMDHMS);
    }

    public static String curFormatDate(String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date date = new Date();
        return sdf.format(date);
    }

    public static int getYearTwoBit() {
        Calendar c = Calendar.getInstance();
        String year = String.valueOf(c.get(Calendar.YEAR));
        return Integer.parseInt(year.substring(2, 4));
    }

    public static int getDayOfYear() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.DAY_OF_YEAR);
    }

    public static int curYear() {
        return Integer.parseInt(new SimpleDateFormat("yyyy").format(new Date()));
    }

    public static int curMonth() {
        return Integer.parseInt(new SimpleDateFormat("MM").format(new Date()));
    }

    public static int curDay() {
        return Integer.parseInt(new SimpleDateFormat("dd").format(new Date()));
    }

    public static int curHour() {
        return Integer.parseInt(new SimpleDateFormat("HH").format(new Date()));
    }

    public static int curMinute() {
        return Integer.parseInt(new SimpleDateFormat("mm").format(new Date()));
    }

    /**
     * 获取指定日期的毫秒数
     */
    public static long getMillis(Date date) {
        java.util.Calendar c = java.util.Calendar.getInstance();
        c.setTime(date);
        return c.getTimeInMillis();
    }

    /**
     * 获取月份的最后一天
     * @param date 日期
     * @return 日期
     */
    public static Date lastDayOfMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int value = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        cal.set(Calendar.DAY_OF_MONTH, value);
        return cal.getTime();
    }

    /**
     * 获取月份的天数
     * @param date 日期
     * @return 天数
     */
    public static int daysOfMonth(Date date) {
        Date d = lastDayOfMonth(date);
        SimpleDateFormat sdf = new SimpleDateFormat("dd");
        return Math.abs(Integer.parseInt(sdf.format(d)));
    }

}
