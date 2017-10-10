package com.rokid.cloudappclient.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.rokid.bean.ActionNode;
import com.rokid.cloudappclient.AppTypeRecorder;
import com.rokid.cloudappclient.service.SirenService;
import com.rokid.cloudappclient.event.SystemServiceHelper;
import com.rokid.cloudappclient.light.LightHelper;
import com.rokid.cloudappclient.tts.TTSHelper;
import com.rokid.light.LightUtils;
import com.rokid.parser.ResponseParser;
import com.rokid.monitor.BaseCloudStateMonitor;
import com.rokid.logger.Logger;
import com.rokid.reporter.EventParamUtils;
import com.rokid.tts.TTSUtils;

import java.io.IOException;
import java.lang.ref.WeakReference;

import rokid.os.RKEventBus;
import rokid.services.util.RemoteServiceHelper;
import tv.danmaku.ijk.media.player.promoter.ErrorPromoter;

/**
 * This is a basic Activity, all the Activity in the project are to extends it.
 * It management common lifecycle and have some common methods to parse intent、NLP and error TTS.
 * <p>
 * Author: fengfan
 * Modified: 2017/06/01
 */
public abstract class BaseActivity extends Activity implements BaseCloudStateMonitor.TaskProcessCallback {

    //只有在cut应用入栈的时候才会调onResume
    boolean isNeedResume;

    private static final String KEY_NLP = "nlp";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.d("activity type: " + getCloudStateMonitor().getFormType() + " OnCreated");
        getCloudStateMonitor().registerContext(new WeakReference<Context>(this));
        getCloudStateMonitor().setTaskProcessCallback(new WeakReference<BaseCloudStateMonitor.TaskProcessCallback>(this));
        AppTypeRecorder.getInstance().storeAppStateManager(getCloudStateMonitor());

        TTSUtils.getInstance().setTtsHelper(new TTSHelper().registerVoiceStateCallback((getCloudStateMonitor())));

        EventParamUtils.setEventParamCreator(new SystemServiceHelper());

        LightUtils.getInstance().setLightHelper(new LightHelper().initLight());

        ErrorPromoter.getInstance().registerContext(new WeakReference<Context>(this));

        getCloudStateMonitor().onCreate();
        isNeedResume = false;
        executeIntent(getIntent());

        Intent sirenService = new Intent(this, SirenService.class);
        startService(sirenService);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Logger.d("activity type: " + getCloudStateMonitor().getFormType() + " onNewIntent");
        executeIntent(intent);
        setIntent(intent);
    }

    private void executeIntent(Intent intent) {
        if (intent == null) {
            Logger.d("intent null !");
            try {
                ErrorPromoter.getInstance().speakErrorPromote(ErrorPromoter.ERROR_TYPE.DATA_INVALID, null);
            } catch (IOException e) {
                e.printStackTrace();
                Logger.e("exception : promote error data invalid ");
            }
            return;
        }

        String actionStr = intent.getStringExtra(KEY_NLP);

        try {
            ActionNode actionNode = ResponseParser.getInstance().parseAction(actionStr);
            getCloudStateMonitor().onNewIntentActionNode(actionNode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Logger.d("activity type: " + getCloudStateMonitor().getFormType() + " onRestart");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Logger.d("activity type: " + getCloudStateMonitor().getFormType() + " onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Logger.d("activity type: " + getCloudStateMonitor().getFormType() + " onResume " + " isNeedResume : " + isNeedResume);
        if (isNeedResume) {
            AppTypeRecorder.getInstance().storeAppStateManager(getCloudStateMonitor());
            getCloudStateMonitor().onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isNeedResume = true;
        Logger.d("activity type: " + getCloudStateMonitor().getFormType() + " onPause");
        getCloudStateMonitor().onPause();

    }

    @Override
    protected void onStop() {
        super.onStop();
        Logger.d("activity type: " + getCloudStateMonitor().getFormType() + " onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getCloudStateMonitor().onDestroy();
        Logger.d("activity type: " + getCloudStateMonitor().getFormType() + " onDestroy");
    }

    @Override
    public void openSiren(String type, boolean pickupEnable, int durationInMilliseconds) {
        Logger.d(" process openSiren ");
        Intent intent = new Intent();
        ComponentName component = new ComponentName("com.rokid.activation", "com.rokid.activation.service.CoreService");
        intent.setComponent(component);
        intent.putExtra("FromType", type);
        intent.putExtra("InputAction", "confirmEvent");
        Bundle bundle = new Bundle();
        bundle.putBoolean("isConfirm", pickupEnable);//拾音打开或关闭
        bundle.putInt("durationInMilliseconds", durationInMilliseconds);//当enable=true时，在用户不说话的情况下，拾音打开持续时间
        intent.putExtra("intent", bundle);
        startService(intent);
    }

    @Override
    public void onTaskFinished() {
        finish();
    }

    @Override
    public void onExitCallback() {
        finish();
    }


    public static final String EXIT_SESSION = "EXIT_SESSION";

    @Override
    public void exitSessionToAppEngine(){

        String appId = getCloudStateMonitor().getmAppId();

        RKEventBus eventBus = RemoteServiceHelper.getService(RemoteServiceHelper.RK_EVENTBUS);
        Bundle bundle = new Bundle();
        bundle.putString("appId", appId);
        eventBus.sendEvent(EXIT_SESSION,bundle,0);
    }

    public abstract BaseCloudStateMonitor getCloudStateMonitor();

}

