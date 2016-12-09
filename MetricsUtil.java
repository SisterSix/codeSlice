package com.dnp.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.stereotype.Component;

/**
 * Copyright 2016 DONOPO Ltd. All rights reserved.
 * <p/>
 * Remark   : SpringBoot 计数器工具
 * <p/>
 * Author   : Tim Mars
 * Project  : Quake
 * Date     : 7/3/16 15:49
 */
@Component
public class MetricsUtil {

    private static CounterService service;

    @Autowired
    public void MyService(CounterService counterService) {
        service = counterService;
    }

    public static void counterMetrics(String metric) {
        service.increment(metric);
    }

}
