package com.dnp.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.UUID;

/**
 * Copyright 2016 DONOPO Ltd. All rights reserved.
 * <p/>
 * Remark   :
 * <p/>
 * Author   : Tim Mars
 * Project  : Quake
 * Date     : 9/16/16 22:43
 */
public class UuidUtil {


    //   id format  =>
//   timestamp |datacenter | sequence
//   41        |10         |  12
    private static final long sequenceBits = 12;
    private static final long datacenterIdBits = 10L;
    private static final long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);

    private static final long datacenterIdShift = sequenceBits;
    private static final long timestampLeftShift = sequenceBits + datacenterIdBits;

    private static final long twepoch = 1288834974657L;
    private static final long datacenterId = getDatacenterId();
    private static final long sequenceMax = 4096;

    private static volatile long lastTimestamp = -1L;
    private static volatile long sequence = 0L;


    /**
     * 格式化唯一编码
     *
     * @param uuid 编码
     * @return 编码
     */
    public static String formatUuid(String uuid) {
        String result = uuid.toUpperCase().replace("-", "");
        if (result.length() != 32) throw new RuntimeException("Wrong UUID");
        return result;
    }


    /**
     * 获取32位唯一编码
     *
     * @return 编码
     */
    public static String getUuid() {
        return UUID.randomUUID().toString().toUpperCase().replace("-", "");
    }

    /**
     * 生成Token
     *
     * @param uuid UUID
     * @return
     */
    public static String getToken(String uuid) {
        return getUuid().substring(0, 4) + uuid.substring(5, 13) + getUuid().substring(0, 4);
    }


    /**
     * 如果返回为0,表示生成Id失败
     * @return
     */
    public static synchronized long generateLongId() {
        if (datacenterId > maxDatacenterId || datacenterId < 0) {
            return 0;
        }
        long timestamp = System.currentTimeMillis();
        if (timestamp < lastTimestamp) {
            return 0;
        }
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) % sequenceMax;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0;
        }
        lastTimestamp = timestamp;
        Long id = ((timestamp - twepoch) << timestampLeftShift) |
                (datacenterId << datacenterIdShift) |
                sequence;
        return id;
    }

    private static long tilNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }


    private static long getDatacenterId() {

        try {
            InetAddress ip = InetAddress.getLocalHost();
            NetworkInterface network = NetworkInterface.getByInetAddress(ip);
            long id;
            if (network == null) {
                id = 1;
            } else {
                byte[] mac = network.getHardwareAddress();
                id = ((0x000000FF & (long) mac[mac.length - 1]) | (0x0000FF00 & (((long) mac[mac.length - 2]) << 8))) >> 6;
            }
            return id;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void main(String[] args) {
        System.out.println(generateLongId());
    }


}
