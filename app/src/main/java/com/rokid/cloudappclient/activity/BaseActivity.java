package com.rokid.cloudappclient.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.rokid.cloudappclient.R;
import com.rokid.cloudappclient.parser.IntentParser;
import com.rokid.cloudappclient.parser.ResponseParser;
import com.rokid.cloudappclient.state.BaseAppStateManager;
import com.rokid.cloudappclient.tts.TTSHelper;
import com.rokid.cloudappclient.tts.TTSSpeakInterface;
import com.rokid.cloudappclient.util.AppTypeRecorder;
import com.rokid.cloudappclient.util.Logger;

/**
 * This is a basic Activity, all the Activity in the project are to extends it.
 * It management common lifecycle and have some common methods to parse intent、NLP and error TTS.
 *
 * Author: fengfan
 * Modified: 2017/06/01
 */
public abstract class BaseActivity extends Activity implements TTSSpeakInterface, BaseAppStateManager.TaskProcessCallback {

    IntentParser intentParser = new IntentParser(this);
    BaseAppStateManager appStateManager = getAppStateManager();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppTypeRecorder.getInstance().storeAppStateManager(appStateManager);
        appStateManager.setTaskProcessCallback(this);
        ResponseParser.getInstance().setTTSSpeakInterface(this);
        intentParser.parseIntent(getIntent());
        Logger.d( "activity type: " + getAppStateManager().getFormType() + " OnCreated");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Logger.d("activity type: " + getAppStateManager().getFormType() +" onNewIntent");
        if (intent == null) {
            Logger.d("intent null !");
            return;
        }
        intentParser.parseIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Logger.d("activity type: " + getAppStateManager().getFormType() +" onRestart");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Logger.d("activity type: " + getAppStateManager().getFormType() +" onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Logger.d("activity type: " + getAppStateManager().getFormType() +" onResume");
        appStateManager.onAppResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Logger.d("activity type: " + getAppStateManager().getFormType() +" onPause");
        appStateManager.onAppPaused();

    }

    @Override
    protected void onStop() {
        super.onStop();
        Logger.d("activity type: " + getAppStateManager().getFormType() +" onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Logger.d("activity type: " + getAppStateManager().getFormType() +" onDestroy");
    }

    /**
     * ------------------TTS REFERENCE START--------------------
     **/
    TTSHelper ttsHelper = TTSHelper.getInstance();

    @Override
    public void speakIntentEmptyErrorTTS() {
        ttsHelper.speakTTSError(getResources().getString(R.string.tts_intent_empty_error));
    }

    @Override
    public void speakNLPEmptyErrorTTS() {
        ttsHelper.speakTTSError(getResources().getString(R.string.tts_nlp_empty_error));
    }

    @Override
    public void speakNLPDataEmptyErrorTTS() {
        ttsHelper.speakTTSError(getResources().getString(R.string.tts_nlp_data_empty_error));
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

