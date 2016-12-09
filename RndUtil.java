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

import java.util.*;

;

/**
 * Remark:
 * <p/>
 * Author: Tim
 * Date: 12/31/13 17:13
 */
public class RndUtil {

    private static final Random random = new Random();

    /**
     * 获取一个整形的整数值
     * @return 结果
     */
    public static int getInt() {
        return random.nextInt();
    }

    /**
     * 获取[0,1)之间double数
     * @return 结果
     */
    public static double getDouble() {
        return random.nextDouble();
    }

    /**
     * 获取[0,1)之前浮点数
     * @return 结果
     */
    public static float getFloat() // action random number from 0 to 1
    {
        return random.nextFloat();
    }

    /**
     * 获取一个[0,max)整形
     * @param lessMax 上限
     * @return 结果
     */
    public static int getInt(int lessMax) {
        return random.nextInt(lessMax);
    }

    /**
     *  获取一个[min,max]整形
     * @param min 最小
     * @param max 最大
     * @return 结果
     */
    public static int getInt(int min, int max) {
        if(min == max)
            return max;
        return random.nextInt(max-min+1)+min;
    }

    /**
     * 获取一个布尔值随机
     * @return 结果
     */
    public static boolean getBool() {
        return random.nextBoolean();
    }

    /**
     * 获取一个整数或者负数
     * @return 1 or -1
     */
    public static int getPosOrNe() {
        if (getBool()) {
            return 1;
        } else {
            return -1;
        }
    }

    /**
     * 获取一个固定随机数生成器,记住用完后一定要归还
     * @param seed 种子
     * @return 固定随机数生成器
     */
    public static FixRandom getFixRandom(long seed) {
        FixRandom fixRandom = FixRandom.get();
        fixRandom.setSeed(seed);
        return  fixRandom;
    }


    /**
     * 在0-max之间随机获取n个不同的数
     * @param max 最大
     * @param count 数量
     */
    public static List<Integer> getDiffInt(int max, int count) {
        if (count > max) return null;
        Set<Integer> set = new HashSet<>();
        List<Integer> list = new ArrayList<>();
        Random random = new Random();
        while (set.size() < count) {
            int t = random.nextInt(max);
            if (set.add(t))
                list.add(t);
        }
        return list;
    }

    public static void main(String[] args) {
//        List<Integer> set = getDiffInt(5, 5);
//        for (Integer key : set) {
//            System.out.println(key);
//        }

//        for (int i=0; i< 20; i++) {
//            System.out.println(getInt(10,20));
//            System.out.println(random.nextInt(11) + 10);
//        }

//        long count1 = System.currentTimeMillis();
//        for (int i=0; i< 10000000; i++) {
//            int a = getInt(10, 20);
//        }
//        System.out.println(":::: " + (System.currentTimeMillis() - count1));
//
//        long count2 = System.currentTimeMillis();
//        for (int i=0; i< 10000000; i++) {
//            int a = (int) Math.random() * 10 + 10;
//        }
//        System.out.println(":::: " + (System.currentTimeMillis() - count2));

        int cardNum = 54;
        int drawNum = 54;

        long count1 = System.currentTimeMillis();
        for (int i=0; i< 100000; i++) {
            DealBox diff = DealBox.get(cardNum);
            for (int j=0;j<drawNum;j++) diff.next();
            diff.remand();
        }
        System.out.println(":::: " + (System.currentTimeMillis() - count1));

        long count3 = System.currentTimeMillis();
        for (int i=0; i< 100000; i++) {
            List<Integer> set = getDiffInt(cardNum, drawNum);
        }
        System.out.println(":::: " + (System.currentTimeMillis() - count3));

//        long count2 = System.currentTimeMillis();
//        for (int i=0; i< 100000; i++) {
//            List<Integer> set = getDiffInt(cardNum, drawNum);
//        }
//        System.out.println(":::: " + (System.currentTimeMillis() - count2));
//
//        long count4 = System.currentTimeMillis();
//        for (int i=0; i< 100000; i++) {
//            List<Integer> set = getDiffInt(cardNum, drawNum);
//        }
//        System.out.println(":::: " + (System.currentTimeMillis() - count4));
//
//        long count5 = System.currentTimeMillis();
//        for (int i=0; i< 100000; i++) {
//            List<Integer> set = getDiffInt(cardNum, drawNum);
//        }
//        System.out.println(":::: " + (System.currentTimeMillis() - count5));

    }


}
