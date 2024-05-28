package com.coderman.club.utils;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;

import java.net.Proxy;
import java.util.concurrent.TimeUnit;

public class OkHttpUtil {

    private static volatile OkHttpClient okHttpClient;

    public static ConnectionPool connectionPool = new ConnectionPool(10, 5, TimeUnit.MINUTES);

    public static OkHttpClient getInstance() {

        if (okHttpClient == null) {
            synchronized (OkHttpClient.class) {
                if (okHttpClient == null) {
                    okHttpClient = new OkHttpClient.Builder()
                            //来屏蔽系统代理
                            .proxy(Proxy.NO_PROXY)
                            .connectionPool(connectionPool)
                            //连接超时
                            .connectTimeout(600, TimeUnit.SECONDS)
                            //写入超时
                            .writeTimeout(600, TimeUnit.SECONDS)
                            //读取超时
                            .readTimeout(600, TimeUnit.SECONDS)
                            .build();
                    okHttpClient.dispatcher().setMaxRequestsPerHost(200);
                    okHttpClient.dispatcher().setMaxRequests(200);
                }
            }
        }
        return okHttpClient;
    }

}
