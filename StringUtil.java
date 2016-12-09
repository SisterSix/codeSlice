/*
 * The MIT License (MIT)
 *   Copyright (c) 2013 DONOPO Studio
 *
 *   Permission is hereby granted, free of charge, to any person obtaining a copy
 *   of this software and associated documentation files (the "Software"), to deal
 *   in the Software without restriction, including without limitation the rights
 *   to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *   copies of the Software, and to permit persons to whom the Software is
 *   furnished to do so, subject to the following conditions:
 *
 *   The above copyright notice and this permission notice shall be included in
 *   all copies or substantial portions of the Software.
 */

package com.dnp.util;


import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Remark: 字符串工具类,主要用来格式化显示类字符.
 * <p/>
 * Author: Tim
 * Date: 9/28/13 3:25 PM
 */
public class StringUtil {

    /**
     * 生成固定长度补零(0)的字符串
     * @param number 数字
     * @param width 宽度
     * @return 结果
     */
    public static String genFixWidthNumber(int number, int width) {
        String result = Integer.toString(number);
        return fixWidthString("0",width - result.length()) + result;
    }

    /**
     * 获取在中间的固定长度的字符串
     * @param str 字符串
     * @param width 宽度
     * @return 结果
     */
    public static String fixWidthStringByCenter(String str, int width) {
        if (str.length() > width) {
            str = bSubstring(str, width);
        } else {
            int len = (width - wordLength(str)) / 2;
            str = fixWidthString(" ", len) + str + fixWidthString(" ", (width - wordLength(str)) - len + 1); //str.length()
        }
        return str;
    }

    /**
     * 获取靠右的固定长度字符串
     * @param str 字符串
     * @param width 宽度
     * @return 结果
     */
    public static String fixWidthStringByRight(String str, int width) {
        if (str.length() > width) {
            str = bSubstring(str, width);
        } else {
            str = String.format("%" + width + "s", str);
            str = str.replaceAll("\\s", " ");
        }
        return str;
    }

    /**
     * 获取靠左的固定长度字符串
     * @param str 字符串
     * @param width 宽度
     * @return 结果
     */
    public static String fixWidthStringByLeft(String str, int width) {
        if (str.length() > width) {
            str = bSubstring(str, width);
        } else {
            str = str + fixWidthString(" ", width - wordLength(str)); //str.length()
        }
        return str;
    }

    /**
     * 获取给定模式的固定长度的字符串
     * @param patten 模板
     * @param width 宽度
     * @return 结果
     */
    public static String fixWidthString(String patten, int width) {
        if (width == 0) return "";
        StringBuffer sb = new StringBuffer(width);
        int len = patten.length();
        for (int i = 0; i <= width / len; i++) {
            sb.append(patten);
        }
        return sb.toString().substring(0, width - 1);
    }

    /**
     * 用Box包围的字符串组
     * @param list 字符串组
     * @param width 宽度
     * @param prefix 前缀
     * @return 结果
     */
    public static String boxString(Collection<String> list, int width, String prefix) {
        StringBuffer sb = new StringBuffer("+");
        int pre = 0;
        if (null != prefix) {
            pre = prefix.length();
        } else {
            prefix = "";
        }
        sb.append(fixWidthString("-", width)).append("+\n");
        for (String o : list) {
            sb.append("|").append(prefix).append(fixWidthStringByLeft(o, width - pre)).append("|\n");
        }
        sb.append("+").append(fixWidthString("-", width)).append("+\n");
        return sb.toString();
    }

    /**
     * 用Box包围的字符串
     * @param content 字符串
     * @param width 宽度
     * @param prefix 前缀
     * @return 结果
     */
    public static String boxString(String content, int width, String prefix) {
        StringBuffer sb = new StringBuffer("+");
        int pre = 0;
        if (null != prefix) {
            pre = prefix.length();
        } else {
            prefix = "";
        }
        sb.append(fixWidthString("-", width)).append("+\n");
        sb.append("|").append(prefix).append(fixWidthStringByCenter(content, width - pre)).append("|\n");
        sb.append("+").append(fixWidthString("-", width)).append("+\n");
        return sb.toString();
    }

    /**
     * 字符串按"字"计算长度
     * @param s 字符串
     * @return 结果
     */
    public static int wordLength(String s) {
        int length = 0;
        for (int i = 0; i < s.length(); i++) {
            int ascii = Character.codePointAt(s, i);
            if (ascii >= 0 && ascii <= 255)
                length++;
            else
                length += 2;

        }
        return length;
    }

    /**
     * 按"字"截取字符串
     * @param s 字符串
     * @param length 长度
     * @return 结果
     */
    public static String bSubstring(String s, int length) {

        byte[] bytes = new byte[0];
        try {
            bytes = s.getBytes("Unicode");
        } catch (UnsupportedEncodingException e) {
            //nop;
        }
        int n = 0; // 表示当前的字节数
        int i = 2; // 要截取的字节数，从第3个字节开始
        for (; i < bytes.length && n < length; i++) {
            // 奇数位置，如3、5、7等，为UCS2编码中两个字节的第二个字节
            if (i % 2 == 1) {
                n++; // 在UCS2第二个字节时n加1
            } else {
                // 当UCS2编码的第一个字节不等于0时，该UCS2字符为汉字，一个汉字算两个字节
                if (bytes[i] != 0) {
                    n++;
                }
            }
        }
        // 如果i为奇数时，处理成偶数
        if (i % 2 == 1)

        {
            // 该UCS2字符是汉字时，去掉这个截一半的汉字
            if (bytes[i - 1] != 0)
                i = i - 1;
                // 该UCS2字符是字母或数字，则保留该字符
            else
                i = i + 1;
        }

        try {
            return new String(bytes, 0, i, "Unicode");
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    /**
     * 判断是否是驼峰命名
     *
     * @param param 源
     * @return 结果
     */
    public static boolean isCamel(String param) {
        Pattern p = Pattern.compile("[A-Z]");
        if (param == null || param.equals("")) {
            return true;
        }
        return false;
    }

    /**
     * 大小写转下划线
     *
     * @param param 源
     * @return 结果
     */
    public static String camel4underscore(String param) {
        Pattern p = Pattern.compile("[A-Z]");
        if (param == null || param.equals("")) {
            return "";
        }
        StringBuilder builder = new StringBuilder(param);
        Matcher mc = p.matcher(param);
        int i = 0;
        while (mc.find()) {
            builder.replace(mc.start() + i, mc.end() + i, "_" + mc.group().toLowerCase());
            i++;
        }

        if ('_' == builder.charAt(0)) {
            builder.deleteCharAt(0);
        }
        return builder.toString();
    }

    /**
     * 下划线转大小写
     *
     * @param param 源
     * @return 结果
     */
    public static String underscore4Camel(String param) {
        String[] strs = param.split("_");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < strs.length; i++) {
            sb.append(prefixUpperCase(strs[i]));
        }
        return sb.toString();
    }

    /**
     * 是否全是数字
     * @param str 输入
     * @return 为真
     */
    public static boolean isAllNumeric(String str) {
        if (str == null){
            return false;
        }
        String format = "^-?\\d+$";
        Pattern p = Pattern.compile(format, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(str);
        return m.matches();
    }

    /**
     * 是否是无符号整数
     * @param str 输入
     * @return 为真
     */
    public static boolean isUnSignedNumber(String str) {
        if (str == null) {
            return false;
        }
        String format = "^\\d+$";
        Pattern p = Pattern.compile(format, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(str.trim());
        return m.matches();
    }

    /**
     * 解析字符串为数字
     * @param str 数字
     * @return 结果,解析失败为0
     */
    public static int str2Int(String str) {
        if (isAllNumeric(str)) {
            return Integer.parseInt(str);
        }
        return 0;
    }

    /**
     * 解析字符串为数字
     * @param str 数字
     * @return 结果,解析失败为0
     */
    public static float str2Float(String str) {
        if (str != null) {
            try {
                return Float.parseFloat(str.trim());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    /**
     * 获取整形的二进制字符串
     * @param l 输入
     * @return 结果
     */
    public static String binaryInt(int l) {
        StringBuffer rs = new StringBuffer(32);
        for (int i = 31; i >= 0; i--)
            if ((1L << i & (long) l) != 0L)
                rs.append(1);
            else
                rs.append(0);

        return rs.toString();
    }

    /**
     * 获取长整形的二进制字符串
     * @param l 输入
     * @return 结果
     */
    public static String binaryLong(long l) {
        StringBuffer rs = new StringBuffer(64);
        for (int i = 63; i >= 0; i--)
            if ((1L << i & l) != 0L)
                rs.append(1);
            else
                rs.append(0);

        return rs.toString();
    }

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    /**
     * 将字符数组格式化成十六进制
     * @param bytes 二进制数组
     * @return 十六进制结果
     */
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     * 将字符数组格式化成十六进制
     * @param bytes 二进制数组
     * @param len 结束位置
     * @return 十六进制结果
     */
    public static String bytesToHex(byte[] bytes, int len) {
        char[] hexChars = new char[len * 2];
        for ( int j = 0; j < len; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     * 首字母大写
     * @param str 字符串
     * @return 结果
     */
    public static String prefixUpperCase(String str) {
        byte[] items = str.getBytes();
        items[0] =  (byte)((char)items[0]-'a'+'A');
        return new String(items);
    }


    /**
     * 首字母小写
     * @param str 字符串
     * @return 结果
     */
    public static String prefixLowerCase(String str) {
        byte[] items = str.getBytes();
        items[0] =  (byte)((char)items[0]+'a'-'A');
        return new String(items);
    }


}
