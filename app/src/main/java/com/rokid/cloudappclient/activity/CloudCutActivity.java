package com.rokid.cloudappclient.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.rokid.cloudappclient.state.BaseAppStateManager;
import com.rokid.cloudappclient.state.CutAppStateManager;

public class CloudCutActivity extends BaseActivity {

    @Override
    public BaseAppStateManager getAppStateManager() {
        return CutAppStateManager.getInstance();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("jiabin","cut onCreate :" + getTaskId());
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.d("jiabin","cut onNewIntent :" + getTaskId());
        super.onNewIntent(intent);
    }

    @Override
    protected void onResume() {
        Log.d("jiabin","cut onResume :" + getTaskId());
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d("jiabin","cut onPause :" + getTaskId());
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Log.d("jiabin","cut onDestroy :" + getTaskId());
        super.onDestroy();
    }

    @Override
    public void onAllTaskFinished() {
        Log.d("jiabin","cut onAllTaskFinished :" + getTaskId());
        super.onAllTaskFinished();
    }

    @Override
    public void onExitCallback() {
        Log.d("jiabin","cut onExitCallback :" + getTaskId());
        super.onExitCallback();
    }
}
