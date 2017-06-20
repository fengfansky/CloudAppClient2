package com.rokid.cloudappclient.util;

import com.rokid.cloudappclient.state.BaseAppStateManager;

/**
 * Created by fanfeng on 2017/6/16.
 */

public class AppTypeRecorder {

    private BaseAppStateManager appStateManager;

    public static AppTypeRecorder getInstance(){
        return AppTypeRecorder.AppTypeRecorderHoler.instance;
    }

    private static class AppTypeRecorderHoler{
        private static final AppTypeRecorder instance = new AppTypeRecorder();
    }

    public BaseAppStateManager getAppStateManager() {
        return appStateManager;
    }

    public void storeAppStateManager(BaseAppStateManager appStateManager) {
        this.appStateManager = appStateManager;
    }

}
