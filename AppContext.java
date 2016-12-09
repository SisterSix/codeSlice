package com.dnp.util;

import com.dnp.AmmsApplication;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;


/**
 * Copyright 2016 DONOPO Ltd. All rights reserved.
 * <p>
 * Remark   : 获取对于SpringIoC管理的Bean
 * <p/>
 * Author   : Tim Mars
 * Project  : Quake
 * Date     : 6/22/2016
 */
@Component
public class AppContext implements ApplicationContextAware {

    private static ApplicationContext ctx;
    private static AppContext instance = new AppContext();

    private AppContext() {
    }

    public static AppContext get() {
        return instance;
    }

    public static <T> T getBean(Class<T> tClass) {
        if (AppContext.ctx == null) {
            throw new NullPointerException("ctx not set");
        }
        try {
            return AppContext.ctx.getBean(tClass);
        } catch (Exception e) {
            return AmmsApplication.getCtx().getBean(tClass);
        }
    }

    public static Object getBean(String name) {
        if (AppContext.ctx == null) {
            throw new NullPointerException("ctx not set");
        }
        return AppContext.ctx.getBean(name);
    }

    public ApplicationContext getCtx() {
        return ctx;
    }

    public synchronized void setCtx(ApplicationContext ctx) {
        if (ctx == null) {
            throw new NullPointerException();
        }
        AppContext.ctx = ctx;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        AppContext.ctx = applicationContext;
    }

}
