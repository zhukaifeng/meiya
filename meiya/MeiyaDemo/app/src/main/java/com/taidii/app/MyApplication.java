package com.taidii.app;

import android.app.Application;

import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.log.LoggerInterceptor;

import okhttp3.OkHttpClient;

/**
 * Created by zhukaifeng on 2018/11/30.
 */

public class MyApplication extends Application {

    public static IWXAPI mWxApi;
    public static MyApplication instance;

    public static MyApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new LoggerInterceptor("TAG"))
                //其他配置
                .build();
        OkHttpUtils.initClient(okHttpClient);
        registerToWX();
    }

    private void registerToWX() {
        mWxApi = WXAPIFactory.createWXAPI(this, Constants.APP_ID, false);
        mWxApi.registerApp(Constants.APP_ID);
    }
}

