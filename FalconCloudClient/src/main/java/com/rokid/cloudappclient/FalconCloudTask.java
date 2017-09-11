package com.rokid.cloudappclient;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.rokid.cloudappclient.tts.TTSHelper;
import com.rokid.http.BaseUrlConfig;
import com.rokid.http.HttpClientWrapper;
import com.rokid.cloudappclient.state.CloudCutState;
import com.rokid.cloudappclient.state.CloudSceneState;
import com.rokid.bean.ActionNode;
import com.rokid.bean.response.responseinfo.action.ActionBean;
import com.rokid.monitor.BaseCloudStateMonitor;
import com.rokid.parser.ResponseParser;
import com.rokid.logger.Logger;
import com.rokid.tts.TTSUtils;

import rokid.app.TaskBundle;
import rokid.context.RKBaseTask;
import rokid.context.RokidState;
import rokid.context.utils.NlpMockUtils;
import rokid.event.CVInputEvent;
import rokid.event.SensorInputEvent;
import rokid.event.TouchInputEvent;
import rokid.event.VoiceCommand;
import rokid.event.VoiceInputEvent;
import tv.danmaku.ijk.media.player.promoter.ErrorPromoter;

import java.io.IOException;
import java.lang.ref.WeakReference;

/**
 * Modified by fanfeng on 2017/7/20.
 */
public class FalconCloudTask extends RKBaseTask implements BaseCloudStateMonitor.TaskProcessCallback{

    @Override
    public void onCreate(TaskBundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TTSUtils.setTtsHelper(new TTSHelper());
        BaseUrlConfig.initDeviceInfo();
        getCloudStateMonitor().setTaskProcessCallback(new WeakReference<BaseCloudStateMonitor.TaskProcessCallback>(this));
        ErrorPromoter.getInstance().initRKAudioPlayer(new WeakReference<Context>(this));
        NlpMockUtils.setMockNlp(true);
    }

    @Override
    public void onVoiceCommand(VoiceCommand voiceCommand) {
        if (voiceCommand == null) {
            Logger.d("voiceCommand is null !");
            return;
        }
        String nlp = voiceCommand.getNLP();
        String asr = voiceCommand.getASR();
        String action = voiceCommand.getAction();
        Logger.d(" nlp " + nlp);
        Logger.d(" asr " + asr);
        Logger.d(" action " + action);

        ActionNode actionNode = null;

        try {
            ResponseParser.getInstance().parseAction(action);
        } catch (IOException e) {
            Logger.e(" speak error info exception !");
            e.printStackTrace();
        }

        if (actionNode == null) {
            try {
                ErrorPromoter.getInstance().speakErrorPromote(ErrorPromoter.ERROR_TYPE.DATA_INVALID, null);
            } catch (IOException e) {
                Logger.e(" speak error info exception !");
                e.printStackTrace();
            }
            return;
        }

        switch (actionNode.getForm()) {
            case ActionBean.FORM_SCENE:
                startState(CloudSceneState.class, voiceCommand);
                break;
            case ActionBean.FORM_CUT:
                startState(CloudCutState.class, voiceCommand);
                break;
        }
    }

    @Override
    public void setContentView(View view) {

    }

    @Override
    public void onTaskResult(TaskBundle taskBundle) {

    }

    @Override
    protected RokidState getState() {

        return super.getState();
    }

    public BaseCloudStateMonitor getCloudStateMonitor() {
        if (getState() == null) {
            Logger.d(" rokidState is null !");
            return null;
        }

        Logger.d(" getState() is  " + getState().getStateType());

        if (getState() instanceof CloudSceneState) {
            return ((CloudSceneState) getState()).getCloudStateMonitor();
        } else if (getState() instanceof CloudCutState) {
            return ((CloudCutState) getState()).getCloudStateMonitor();
        }

        return null;
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
    public void onInvalidateState() {
        //TODO 区分scene/cut处理

    }

    @Override
    public void onAllTaskFinished() {
        //TODO 区分scene/cut处理

    }

    @Override
    public void onExitCallback() {
        //TODO 区分scene/cut处理

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        HttpClientWrapper.getInstance().close();
    }

    @Override
    public void onRapture() {

    }


    @Override
    protected void handleOnBindIntent(Intent intent) {

    }

    @Override
    public void onTouchInputEvent(TouchInputEvent touchInputEvent) {

    }

    @Override
    public void onCVInputEvent(CVInputEvent cvInputEvent) {

    }

    @Override
    public void onSensorInputEvent(SensorInputEvent sensorInputEvent) {

    }

    @Override
    public void onVoiceInputEvent(VoiceInputEvent voiceInputEvent) {

    }
}
