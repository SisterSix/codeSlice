package com.dnp.util;

import com.dnp.core.conf.Debug;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.CharsetUtil;
import org.eclipse.jetty.util.ConcurrentHashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.dnp.util.ansi.Ansi.Color.GREEN;
import static com.dnp.util.ansi.Ansi.ansi;

/**
 * Copyright 2016 DONOPO Ltd. All rights reserved.
 * <p/>
 * Remark   : 调试专用日志工具,可以在运行期直接关闭
 * <p/>
 * Author   : Tim Mars
 * Project  : Quake
 * Date     : 7/30/16 14:35
 */
public class LogUtil {

    private static final Logger buglog = LoggerFactory.getLogger("buglog");
    private static final Logger bizlog = LoggerFactory.getLogger("bizlog");

    private static Set<ChannelHandlerContext> cmdCtx = new ConcurrentHashSet<>();

    /**
     * 业务日志专用方法,用来产生可以跟踪的业务日志
     *
     * @param type 业务日志类型
     * @param map  日志组
     */
    @SuppressWarnings("unchecked")
    public static void biz(String type, Map<String, Object> map) {
        Map result = new HashMap<String, Object>();
        result.put("type", type);
        result.put("value", map);
        bizlog.info("{}", JsonUtil.toJson(result));
    }

    /**
     * 记录错误日志
     * @param prefix 前缀
     * @param arguments 参数
     */
    public static void error(String prefix, Object... arguments) {
        LogUtil.debug("[{}] [ERROR] {}", prefix.toUpperCase(), arguments);
    }

    /**
     * 记录SQL日志
     * @param sql 前缀
     */
    public static void sql(String sql) {
        LogUtil.debug("[SQL] {}", sql);
    }

    /**
     * 调试专用日志方法
     *
     * @param format    模板
     * @param arguments 参数
     */
    public static void debug(String format, Object... arguments) {
        if (Debug.sys()) {
            String result = ansi()
                    .fgBright(GREEN)
                    .a(MessageFormatter.arrayFormat(format, arguments).getMessage())
                    .reset()
                    .toString();
            console(result);

            buglog.debug(format, arguments);
        }
    }

    // 从Cmdor输出调试信息
    private static void console(String str) {
        for (ChannelHandlerContext ctx : cmdCtx) {
            try {
                ctx.writeAndFlush(Unpooled.copiedBuffer(
                        "\n-----------------------------------------------------------------------------\n" + str,
                        CharsetUtil.UTF_8));
            } catch (Exception e) {
                cmdCtx.remove(ctx);
            }
        }
    }

    // 注册调试信息组
    public static void registerCtx(ChannelHandlerContext ctx) {
        cmdCtx.add(ctx);
    }

    // 移除调试信息组
    public static void removeCtx(ChannelHandlerContext ctx) {
        cmdCtx.remove(ctx);
    }


}
