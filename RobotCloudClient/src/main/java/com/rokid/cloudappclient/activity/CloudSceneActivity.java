package com.rokid.cloudappclient.activity;

import com.rokid.monitor.BaseCloudStateMonitor;
import com.rokid.monitor.CloudSceneStateMonitor;

public class CloudSceneActivity extends BaseActivity {

    @Override
    public BaseCloudStateMonitor getCloudStateMonitor() {
        return CloudSceneStateMonitor.getInstance();
    }

}
