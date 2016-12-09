package com.dnp.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * Copyright 2016 DONOPO Ltd. All rights reserved.
 * <p>
 * Remark   : MD5获取工具类
 * <p/>
 * Author   : Tim Mars
 * Project  : Quake
 * Date     : 6/22/2016
 */
public class MD5Util {
    protected static char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    protected static MessageDigest messagedigest = null;
    static Map<String, String> initialMd5Map = new HashMap<>();
    private static final String SCRIPT_DIR = "activity";

    static {
        try {
            messagedigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException nsaex) {
            System.err.println(MD5Util.class.getName() + "初始化失败，MessageDigest不支持MD5Util。");
            nsaex.printStackTrace();
        }
    }

    public static String getFileMD5String(File file) throws IOException {
        try (FileInputStream in = new FileInputStream(file)) {
            FileChannel ch = in.getChannel();
            MappedByteBuffer byteBuffer = ch.map(FileChannel.MapMode.READ_ONLY, 0, file.length());
            messagedigest.update(byteBuffer);
            return bufferToHex(messagedigest.digest());
        }
    }

    public static String getMD5String(String s) {
        return getMD5String(s.getBytes());
    }

    public static String getMD5String(byte[] bytes) {
        messagedigest.update(bytes);
        return bufferToHex(messagedigest.digest());
    }

    private static String bufferToHex(byte bytes[]) {
        return bufferToHex(bytes, 0, bytes.length);
    }

    private static String bufferToHex(byte bytes[], int m, int n) {
        StringBuffer stringbuffer = new StringBuffer(2 * n);
        int k = m + n;
        for (int l = m; l < k; l++) {
            appendHexPair(bytes[l], stringbuffer);
        }
        return stringbuffer.toString();
    }

    private static void appendHexPair(byte bt, StringBuffer stringbuffer) {
        char c0 = hexDigits[(bt & 0xf0) >> 4];
        char c1 = hexDigits[bt & 0xf];
        stringbuffer.append(c0);
        stringbuffer.append(c1);
    }

    public static boolean checkPassword(String password, String md5PwdStr) {
        String s = getMD5String(password);
        return s.equals(md5PwdStr);
    }

    public static void main(String[] args) throws IOException {

        File dir = new File(SCRIPT_DIR);
        File[] scriptFiles = dir.listFiles();
        for (File scriptFile : scriptFiles) {
            String md5 = MD5Util.getFileMD5String(scriptFile);
            String filePreMd5 = initialMd5Map.get(scriptFile.getName());
            if (!(md5.equals(filePreMd5))) {
                System.out.println("不同");
                initialMd5Map.put(scriptFile.getName(), md5);
            }
            System.out.println(scriptFile.getName() + ": " + initialMd5Map.get(scriptFile.getName()));
        }

    }
}
