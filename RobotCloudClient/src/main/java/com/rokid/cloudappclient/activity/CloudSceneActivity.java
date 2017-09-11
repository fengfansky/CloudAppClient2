package com.rokid.cloudappclient.activity;


import android.content.Context;

import com.rokid.monitor.BaseCloudStateMonitor;
import com.rokid.monitor.CloudSceneStateMonitor;
import com.rokid.logger.Logger;

import java.lang.ref.WeakReference;

public class CloudSceneActivity extends BaseActivity {

    @Override
    public BaseCloudStateMonitor getCloudStateMonitor() {
        return CloudSceneStateMonitor.getInstance().registerContext(new WeakReference<Context>(this));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onInvalidateState() {
        finish();
    }

    @Override
    public void onAllTaskFinished() {
        Logger.d(" scene don't finish app ");
    }
}
