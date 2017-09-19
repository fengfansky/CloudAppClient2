package com.rokid.cloudappclient;

import com.rokid.monitor.BaseCloudStateMonitor;

/**
 * Created by fanfeng on 2017/6/16.
 */

public class AppTypeRecorder {

    private BaseCloudStateMonitor appStateManager;

    public static AppTypeRecorder getInstance() {
        return AppTypeRecorderHolder.instance;
    }

    private static class AppTypeRecorderHolder {
        private static final AppTypeRecorder instance = new AppTypeRecorder();
    }

    public void storeAppStateManager(BaseCloudStateMonitor appStateManager) {
        this.appStateManager = appStateManager;
    }

    public BaseCloudStateMonitor getAppStateManager() {
        return appStateManager;
    }

    public String getCloudAppStatus(){
        if (appStateManager != null ) {
            return appStateManager.getCloudStatus();
        }else {
            return null;
        }
    }

}
