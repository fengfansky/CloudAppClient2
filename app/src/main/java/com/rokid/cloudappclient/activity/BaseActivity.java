package com.rokid.cloudappclient.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;

import com.rokid.cloudappclient.http.BaseUrlConfig;
import com.rokid.cloudappclient.parser.IntentParser;
import com.rokid.cloudappclient.state.BaseAppStateManager;
import com.rokid.cloudappclient.state.AppTypeRecorder;
import com.rokid.cloudappclient.util.Logger;

import java.io.IOException;

/**
 * This is a basic Activity, all the Activity in the project are to extends it.
 * It management common lifecycle and have some common methods to parse intent、NLP and error TTS.
 * <p>
 * Author: fengfan
 * Modified: 2017/06/01
 */
public abstract class BaseActivity extends Activity implements BaseAppStateManager.TaskProcessCallback {

    IntentParser intentParser = new IntentParser();

    //只有在cut应用入栈的时候才会调onResume
    boolean isNeedResume;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.d("activity type: " + getAppStateManager().getFormType() + " OnCreated");
        AppTypeRecorder.getInstance().storeAppStateManager(getAppStateManager());
        getAppStateManager().setTaskProcessCallback(this);
        isNeedResume = false;
        try {
            intentParser.parseIntent(getIntent());
        } catch (IOException e) {
            Logger.e("exception : promote error data invalid ");
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Logger.d("activity type: " + getAppStateManager().getFormType() + " onNewIntent");
        if (intent == null) {
            Logger.d("intent null !");
            return;
        }
        isNeedResume = false;
        try {
            intentParser.parseIntent(intent);
        } catch (IOException e) {
            Logger.e("exception : promote error data invalid ");
        }
        setIntent(intent);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Logger.d("activity type: " + getAppStateManager().getFormType() + " onRestart");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Logger.d("activity type: " + getAppStateManager().getFormType() + " onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Logger.d("activity type: " + getAppStateManager().getFormType() + " onResume " + " isNeedResume : " + isNeedResume);
        if (isNeedResume) {
            getAppStateManager().onAppResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isNeedResume = true;
        Logger.d("activity type: " + getAppStateManager().getFormType() + " onPause");
        getAppStateManager().onAppPaused();

    }

    @Override
    protected void onStop() {
        super.onStop();
        Logger.d("activity type: " + getAppStateManager().getFormType() + " onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getAppStateManager().onAppDestory();
        Logger.d("activity type: " + getAppStateManager().getFormType() + " onDestroy");
    }

    @Override
    public void openSiren() {
        Logger.d(" process confirm ");
        Intent intent = new Intent();
        ComponentName compontent = new ComponentName("com.rokid.activation", "com.rokid.activation.service.CoreService");
        intent.setComponent(compontent);
        intent.putExtra("InputAction", "confirmEvent");
        Bundle bundle = new Bundle();
        bundle.putInt("isConfirm", 1);   //isConfirm  参数 目前支持 1: 打开拾音 , 0: 关闭拾音
        intent.putExtra("intent", bundle);
        startService(intent);
    }

    @Override
    public void onAllTaskFinished() {
        finish();
    }

    @Override
    public void onExitCallback() {
        finish();
    }

    public abstract BaseAppStateManager getAppStateManager();

}

