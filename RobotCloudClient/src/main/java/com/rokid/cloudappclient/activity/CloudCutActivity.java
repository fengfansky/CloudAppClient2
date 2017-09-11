package com.rokid.cloudappclient.activity;

import android.content.Context;

import com.rokid.monitor.BaseCloudStateMonitor;
import com.rokid.monitor.CloudCutStateMonitor;

import java.lang.ref.WeakReference;

public class CloudCutActivity extends BaseActivity {

    @Override
    public BaseCloudStateMonitor getCloudStateMonitor() {
        return CloudCutStateMonitor.getInstance().registerContext(new WeakReference<Context>(this));
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
        finish();
    }
}
