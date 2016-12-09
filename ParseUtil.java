package com.dnp.util;

/**
 * Copyright 2016 DONOPO Ltd. All rights reserved.
 * <p/>
 * Remark   : 对一些文本进行解析工具
 * <p/>
 * Author   : Tim Mars
 * Project  : Quake
 * Date     : 9/25/16 22:03
 */
public class ParseUtil {

    public static boolean getBool(String bool) {
        if (bool.equalsIgnoreCase("true")) {
            return true;
        } else {
            return false;
        }
    }

    public static int getInt(String integer) {
        try {
            return Integer.parseInt(integer);
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    public static boolean isEmpty(String param) {
        return null == param || param.length() == 0;
    }

    public static boolean isEmpty(int param) {
        return param == 0;
    }

    public static boolean isEmpty(long param) {
        return param == 0;
    }


    public static String getSecuritySql(String sql) {
        sql = sql.replaceAll("'", "''");
        return sql;
    }

}
