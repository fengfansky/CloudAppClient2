package com.rokid.cloudappclient.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.rokid.cloudappclient.state.BaseAppStateManager;
import com.rokid.cloudappclient.state.SceneAppStateManager;

public class CloudSceneActivity extends BaseActivity {

    @Override
    public BaseAppStateManager getAppStateManager() {
        return SceneAppStateManager.getInstance();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("jiabin","scene onCreate :" + getTaskId());
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.d("jiabin","scene onNewIntent :" + getTaskId());
        super.onNewIntent(intent);
    }

    @Override
    protected void onResume() {
        Log.d("jiabin","scene onResume :" + getTaskId());
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d("jiabin","scene onPause :" + getTaskId());
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Log.d("jiabin","scene onDestroy :" + getTaskId());
        super.onDestroy();
    }

    @Override
    public void onAllTaskFinished() {
        Log.d("jiabin","scene onAllTaskFinished :" + getTaskId());
        super.onAllTaskFinished();
    }

    @Override
    public void onExitCallback() {
        Log.d("jiabin","scene onExitCallback :" + getTaskId());
        super.onExitCallback();
    }
}
