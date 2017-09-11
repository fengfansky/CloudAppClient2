package com.rokid.cloudappclient;

import android.app.Application;

import com.rokid.http.BaseUrlConfig;

/**
 * Modified by fanfeng on 2017/7/20.
 */
public class RKCloudAppApplication extends Application {

    private static RKCloudAppApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        BaseUrlConfig.initDeviceInfo();
    }

    public static RKCloudAppApplication getInstance() {
        return instance;
    }

}
