package com.dnp.util;

import org.apache.http.*;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Copyright 2016 DONOPO Ltd. All rights reserved.
 * <p>
 * Remark   : Http客戶端工具
 * <p/>
 * Author   : Tim Mars
 * Project  : Quake
 * Date     : 6/22/2016
 */
public class HttpUtil {

    private static final int TIME_OUT_MIL_SECOND = 1000;

    private static CloseableHttpClient httpClient = null;

    private final static Object syncLock = new Object();

    private static HashMap<String, String> headerMap = new HashMap<>();

    private final static HttpClientContext context = HttpClientContext.create();

    private static void config(HttpRequestBase httpRequestBase) {
        // 设置Header等
        // httpRequestBase.setHeader("User-Agent", "Mozilla/5.0");
        // httpRequestBase
        // .setHeader("Accept",
        // "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        // httpRequestBase.setHeader("Accept-Language",
        // "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");// "en-US,en;q=0.5");
        // httpRequestBase.setHeader("Accept-Charset",
        // "ISO-8859-1,utf-8,gbk,gb2312;q=0.7,*;q=0.7");

        // 配置请求的超时设置
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(TIME_OUT_MIL_SECOND)
                .setConnectTimeout(TIME_OUT_MIL_SECOND)
                .setSocketTimeout(TIME_OUT_MIL_SECOND)
                .build();
        httpRequestBase.setConfig(requestConfig);
    }

    /**
     * 获取HttpClient对象
     */
    private static CloseableHttpClient getHttpClient(String url) {
        String hostname = url.split("/")[2];
        int port = 80;
        if (hostname.contains(":")) {
            String[] arr = hostname.split(":");
            hostname = arr[0];
            port = Integer.parseInt(arr[1]);
        }
        if (httpClient == null) {
            synchronized (syncLock) {
                if (httpClient == null) {
                    httpClient = createHttpClient(100, 100, 100, hostname, port);
                }
            }
        }
        return httpClient;
    }

    //这个线程负责使用连接管理器清空失效连接和过长连接
    private static class IdleConnectionMonitorThread extends Thread {

        private final PoolingHttpClientConnectionManager connMgr;
        private volatile boolean shutdown;

        public IdleConnectionMonitorThread(PoolingHttpClientConnectionManager connMgr) {
            super();
            this.connMgr = connMgr;
        }

        @Override
        public void run() {
            try {
                while (!shutdown) {
                    synchronized (this) {
                        wait(5000);
                        // 关闭失效连接
                        connMgr.closeExpiredConnections();
                        //关闭空闲超过30秒的连接
                        connMgr.closeIdleConnections(30, TimeUnit.SECONDS);
                    }
                }
            } catch (InterruptedException ex) {
            }
        }

        public void shutdown() {
            shutdown = true;
            synchronized (this) {
                notifyAll();
            }
        }
    }

    /**
     * 创建HttpClient对象
     */
    public static CloseableHttpClient createHttpClient(int maxTotal,
                                                       int maxPerRoute, int maxRoute, String hostname, int port) {
        ConnectionSocketFactory plainsf = PlainConnectionSocketFactory
                .getSocketFactory();
        LayeredConnectionSocketFactory sslsf = SSLConnectionSocketFactory
                .getSocketFactory();
        Registry<ConnectionSocketFactory> registry = RegistryBuilder
                .<ConnectionSocketFactory>create().register("msg", plainsf)
                .register("https", sslsf).build();
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(
                registry);
        new IdleConnectionMonitorThread(cm).start();
        // 将最大连接数增加
        cm.setMaxTotal(maxTotal);
        // 将每个路由基础的连接增加
        cm.setDefaultMaxPerRoute(maxPerRoute);
        HttpHost httpHost = new HttpHost(hostname, port);
        // 将目标主机的最大连接数增加
        cm.setMaxPerRoute(new HttpRoute(httpHost), maxRoute);

        // 请求重试处理
        HttpRequestRetryHandler httpRequestRetryHandler = new HttpRequestRetryHandler() {
            public boolean retryRequest(IOException exception,
                                        int executionCount, HttpContext context) {
                if (executionCount >= 2) {// 如果已经重试了2次，就放弃
                    return false;
                }
                if (exception instanceof NoHttpResponseException) {// 如果服务器丢掉了连接，那么就重试
                    return true;
                }
                if (exception instanceof SSLHandshakeException) {// 不要重试SSL握手异常
                    return false;
                }
                if (exception instanceof InterruptedIOException) {// 超时
                    return false;
                }
                if (exception instanceof UnknownHostException) {// 目标服务器不可达
                    return false;
                }
                if (exception instanceof SSLException) {// SSL握手异常
                    return false;
                }

                HttpClientContext clientContext = HttpClientContext
                        .adapt(context);
                HttpRequest request = clientContext.getRequest();
                // 如果请求是幂等的，就再次尝试
                if (!(request instanceof HttpEntityEnclosingRequest)) {
                    return true;
                }
                return false;
            }
        };

        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(cm)
                .setRetryHandler(httpRequestRetryHandler)
                .build();

        return httpClient;
    }

    private static void setPostParams(HttpPost httpost,
                                      Map<String, Object> params) {
        List<NameValuePair> nvps = new ArrayList<>();
        Set<String> keySet = params.keySet();
        for (String key : keySet) {
            nvps.add(new BasicNameValuePair(key, params.get(key).toString()));
        }
        try {
            httpost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * GET请求URL获取内容
     */
    public static String post(String url, HttpEntity httpEntity) {
        HttpPost httppost = new HttpPost(url);
        config(httppost);
        for (String key : headerMap.keySet()) {
            httppost.setHeader(key, headerMap.get(key));
        }
        httppost.setEntity(httpEntity);
        CloseableHttpResponse response = null;
        try {
            response = getHttpClient(url).execute(httppost, context);
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity, "utf-8");
            EntityUtils.consume(entity);
            return result;
        } catch (Exception e) {

            return null;
        } finally {
            try {
                if (response != null)
                    response.close();
            } catch (IOException ignored) {

            }
        }
    }

    public static byte[] postBinary(String url, HttpEntity httpEntity) {
        HttpPost httppost = new HttpPost(url);
        config(httppost);
        for (String key : headerMap.keySet()) {
            httppost.setHeader(key, headerMap.get(key));
        }
        httppost.setEntity(httpEntity);
        CloseableHttpResponse response = null;
        try {
            response = getHttpClient(url).execute(httppost, context);
            HttpEntity entity = response.getEntity();
            byte[] result = EntityUtils.toByteArray(entity);
            EntityUtils.consume(entity);
            return result;
        } catch (Exception e) {
//            e.printStackTrace();
            return null;
        } finally {
            try {
                if (response != null)
                    response.close();
            } catch (IOException ignored) {

            }
        }
    }

    /**
     * GET请求URL获取内容
     */
    public static String get(String url) {
        HttpGet httpget = new HttpGet(url);
        config(httpget);
        CloseableHttpResponse response = null;
        try {
            response = getHttpClient(url).execute(httpget, context );
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity, "utf-8");
            EntityUtils.consume(entity);
            return result;
        } catch (IOException e) {
//            System.out.println(e);
            return null;
        } finally {
            try {
                if (response != null)
                    response.close();
            } catch (IOException ignored) {

            }
        }
    }


}
