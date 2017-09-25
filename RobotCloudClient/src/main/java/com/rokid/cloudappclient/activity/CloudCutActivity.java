package com.rokid.cloudappclient.activity;

import com.rokid.monitor.BaseCloudStateMonitor;
import com.rokid.monitor.CloudCutStateMonitor;

public class CloudCutActivity extends BaseActivity {

    @Override
    public BaseCloudStateMonitor getCloudStateMonitor() {
        return CloudCutStateMonitor.getInstance();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    public void onTaskFinished(boolean shouldEndSession) {
        finish();
        if (shouldEndSession){
            exitSessionToAppEngine();
        }
    }

}
