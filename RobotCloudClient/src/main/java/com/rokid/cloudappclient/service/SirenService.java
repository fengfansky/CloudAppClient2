package com.rokid.cloudappclient.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.rokid.cloudappclient.AppTypeRecorder;
import com.rokid.logger.Logger;
import com.rokid.reporter.BaseReporter;
import com.squareup.okhttp.Response;

public class SirenService extends Service {

    public static final String ACTION_SIREN_STATE_CHANGE = "com.rokid.action.siren.state";

    public static final String PARAM_STATE = "state";



    public SirenService() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {

        this.registerReceiver(sirenReceiver, intentFilter);

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    IntentFilter intentFilter = new IntentFilter(ACTION_SIREN_STATE_CHANGE);

    BroadcastReceiver sirenReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int state = intent.getIntExtra(PARAM_STATE, 0);
            switch (state){
                case 1:
                    //唤醒打断Cut应用的TTS播放
                    AppTypeRecorder.getInstance().onSirenOpened();
                    Logger.d("onReceive siren state awake !");
                    break;
                case 2:
                    //拾音休眠上报
                    Logger.d("onReceive siren state sleep !");
                    AppTypeRecorder.getInstance().onSirenClosed();
                    break;
                default:
                    Logger.d("onReceive siren unknow state : " + state);
                    break;
            }
        }
    };

    BaseReporter.ReporterResponseCallback reporterResponseCallback = new BaseReporter.ReporterResponseCallback() {
        @Override
        public void onEventErrorCallback(String event, BaseReporter.ReporterResponseCallback.ERROR_CODE
        errorCode) {
                Logger.d("onReceive event error : " + event + " error code : " + errorCode);
        }

        @Override
        public void onEventResponseCallback(String event, Response response) {
                Logger.d("onReceive event : " + event + " response : " + response);
        }
    };

}
