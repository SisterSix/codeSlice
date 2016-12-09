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

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Remark:
 * <p/>
 * Author: Tim
 * Date: 10/24/13 12:42 AM
 */

public class FileUtil {

    public static final String DYNAMIC_PATH = "run/";
    public static final String DATA_PATH = "data/";
    public static final String CONFIG_PATH = "conf/";

    /**
     * 将Windows系统和Linux系统的文件路径统一
     * @param fileName 文件名
     * @return 处理后文件名
     */
    public static String format(String fileName) {
        fileName = fileName.replace("\\","/");
        return fileName;
    }

    /**
     * 抽取最终文件路径
     * @param pathName 路径名
     * @return 路径
     */
    public static String extraPathFile(String pathName) {
        String userDir = System.getProperty("user.dir");
        userDir = userDir.replace('\\','/');
        String fileName = pathName.replaceFirst(userDir + "/","");
        return format(fileName);
    }

    /**
     * 抽取文件名
     * @param fullPath 路径名
     * @return 路径
     */
    public static String extraFileName(String fullPath) {
        int pos = fullPath.lastIndexOf("/") + 1;
        return fullPath.substring(pos).trim();
    }

    public static void main(String[] args) {
        try {
            String userDir = System.getProperty("user.dir");
            userDir = userDir.replace('\\','/');
            System.out.println(userDir);
            System.out.println(extraPathFile("/Users/tim/Projects/Javaprj/Matrix/BlazeCommon/data/word/profanity.txt"));
            System.out.println(extraFileName("/Users/tim/Projects/Javaprj/Matrix/BlazeCommon/data/word/profanity.txt"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取文件的内容
     * @param path 路径
     * @param encoding 编码
     * @return 内容
     * @throws IOException
     */
    public static String readFile(String path, Charset encoding)
            throws IOException
    {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return encoding.decode(ByteBuffer.wrap(encoded)).toString();
    }

    /**
     * 获取文件的内容
     * @param file 文件
     * @param in 输入流
     */
    public static void writeFile(File file,InputStream in) {
        if (in == null) {
            return;
        }
        FileOutputStream fout = null;
        try {

            //这里先判断文件夹名是否存在，不存在则建立相应文件夹
            //判断目标文件所在的目录是否存在
            if (!file.getParentFile().exists()) {
                //如果目标文件所在的目录不存在，则创建父目录
                if (!file.getParentFile().mkdirs()) {
                    return;
                }
            }

            // 写入到文件
            fout = new FileOutputStream(file);
            int l;
            byte[] tmp = new byte[1024];
            while ((l = in.read(tmp)) != -1) {
                fout.write(tmp, 0, l);
            }
            fout.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fout != null) {
                try {
                    fout.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    /**
     * 获取文件的内容
     * @param path 路径
     * @param in 输入流
     */
    public static void writeFile(String path,InputStream in) {
        if (in == null) {
            return;
        }
        File file = new File(path);
        writeFile(file,in);
    }

    /**
     * @param ips 输入流
     * @param ops 输出流
     */
    public static void copyStream(InputStream ips, OutputStream ops) {
        byte[] buf = new byte[1024];
        int len;
        try {
            len = ips.read(buf);
            while (len != -1) {
                ops.write(buf, 0, len);
                len = ips.read(buf);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
