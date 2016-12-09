package com.dnp.util;

import java.nio.charset.Charset;

/**
 * Copyright 2016 DONOPO Ltd. All rights reserved.
 *
 * Remark   : 对于字节进行操作的工具类
 * <p/>
 * Author   : Tim Mars
 * Project  : Quake
 * Date     : 6/22/2016
 */
public class BytesUtil {

    /**
     * 写入一个长整形
     *
     * @param src 源
     */
    public static byte[] longToBytes(long src) {
        byte[] bytes = new byte[8];
        for (int i = 0; i < 8; i++) {
//            body[i] = new Long(src & 0xff).byteValue();
            bytes[7 - i] = new Long(src & 0xff).byteValue();
            src = src >> 8;
        }
        return bytes;
    }

    /**
     * 读出一个长整形
     *
     * @return 结果
     */
    public static long bytesToLong(byte[] bytes) {
        long s = 0;
        long s0 = bytes[7] & 0xff;// 最低位
        long s1 = bytes[6] & 0xff;
        long s2 = bytes[5] & 0xff;
        long s3 = bytes[4] & 0xff;
        long s4 = bytes[3] & 0xff;// 最低位
        long s5 = bytes[2] & 0xff;
        long s6 = bytes[1] & 0xff;
        long s7 = bytes[0] & 0xff;

        // s0不变
        s1 <<= 8;
        s2 <<= 16;
        s3 <<= 24;
        s4 <<= 8 * 4;
        s5 <<= 8 * 5;
        s6 <<= 8 * 6;
        s7 <<= 8 * 7;
        s = s0 | s1 | s2 | s3 | s4 | s5 | s6 | s7;
        return s;
    }

    /**
     * 写入一个整形
     *
     * @param src 源
     */
    public static byte[] intToBytes(int src) {
        byte[] body = new byte[4];
        for (int i = 0; i < 4; i++) {
//            body[i] = new Integer(src & 0xff).byteValue();
            body[3 - i] = new Integer(src & 0xff).byteValue();
            src = src >> 8;
        }
        return body;
    }

    /**
     * 读出一个整形
     *
     * @return 结果
     */
    public static int bytesToInt(byte[] bytes) {
        int s = 0;
        int s0 = bytes[3] & 0xff;// 最低位
        int s1 = bytes[2] & 0xff;
        int s2 = bytes[1] & 0xff;
        int s3 = bytes[0] & 0xff;
        s3 <<= 24;
        s2 <<= 16;
        s1 <<= 8;
        s = s0 | s1 | s2 | s3;
        return s;
    }

    public static short bytesToShort(byte[] bytes) {
        short s = 0;
        short s0 = (short) (bytes[1] & 0xff);// 最低位
        short s1 = (short) (bytes[0] & 0xff);
        s1 <<= 8;
        s = (short) (s0 | s1);
        return s;
    }

    /**
     * 写入一个短整形
     *
     * @param src 源
     */
    public static byte[] shortToBytes(short src) {
        byte[] bytes = new byte[2];
        for (int i = 0; i < 2; i++) {
            bytes[1 - i] = new Short((short) (src & 0xff)).byteValue();
            src = (short) (src >> 8);
        }
        return bytes;
    }

    public static byte bytesToByte(byte[] bytes) {
        return bytes[0];
    }

    /**
     * 写入一个短整形
     *
     * @param src 源
     */
    public static byte[] byteToBytes(byte src) {
        return new byte[]{src};
    }

    /**
     * 读出一个字符串序列
     *
     * @return body字符串
     */
    public static String bytesToString(byte[] bytes, Charset charset) {
        return new String(bytes, charset);
    }


    /**
     * 写入一个字符串
     *
     * @param src 源
     */
    public static byte[] stringToBytes(String src, Charset charset) {
        return src.getBytes(charset);
    }


}
