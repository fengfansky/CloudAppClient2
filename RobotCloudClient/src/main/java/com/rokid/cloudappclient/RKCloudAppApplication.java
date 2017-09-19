package com.rokid.cloudappclient;

import android.app.Application;

import com.rokid.reporter.EventParamUtils;

/**
 * Modified by fanfeng on 2017/7/20.
 */
public class RKCloudAppApplication extends Application {

    private static RKCloudAppApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        EventParamUtils.setEventParamCreator(new RuntimeCreator());
    }

    public static RKCloudAppApplication getInstance() {
        return instance;
    }

}
