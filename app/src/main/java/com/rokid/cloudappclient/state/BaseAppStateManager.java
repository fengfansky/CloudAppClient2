package com.rokid.cloudappclient.state;

import android.text.TextUtils;

import com.rokid.cloudappclient.bean.ActionNode;
import com.rokid.cloudappclient.action.MediaAction;
import com.rokid.cloudappclient.action.VoiceAction;
import com.rokid.cloudappclient.bean.response.responseinfo.action.media.MediaBean;
import com.rokid.cloudappclient.bean.response.responseinfo.action.voice.VoiceBean;
import com.rokid.cloudappclient.parser.ResponseParser;
import com.rokid.cloudappclient.reporter.BaseReporter;
import com.rokid.cloudappclient.reporter.MediaReporter;
import com.rokid.cloudappclient.reporter.ReporterManager;
import com.rokid.cloudappclient.reporter.VoiceReporter;
import com.rokid.cloudappclient.util.AppTypeRecorder;
import com.rokid.cloudappclient.util.Logger;
import com.squareup.okhttp.Response;

/**
 * Created by fanfeng on 2017/6/16.
 */

public abstract class BaseAppStateManager implements AppStateCallback, MediaStateCallback, VoiceStateCallback, BaseReporter.ReporterResponseCallBack {

    public ActionNode mActionNode;
    public String mAppId;
    public MediaBean currentMediaBean;
    public VoiceBean currentVoiceBean;

    //表明当此次返回的action执行完后 CloudAppClient 是否要退出，同时，当 shouldEndSession 为 true 时，CloudAppClient 将会忽略 EventRequests，即在action执行过程中不会产生 EventRequest。
    public boolean shouldEndSession;

    public MEDIA_STATE currentMediaState;
    public VOICE_STATE currentVideoState;

    public APP_STATE currentAppState = APP_STATE.APP_RESUME;//默认应用不是暂停状态

    public ReporterManager reporterManager = ReporterManager.getInstance();

    @Override
    public void resetAppState() {
        Logger.d("form: " + getFormType() + " resetAppState ");
        MediaAction.getInstance().stopAction();
        VoiceAction.getInstance().stopAction();
    }

    @Override
    public void onNewActionNode(ActionNode actionNode) {
        if (actionNode != null) {
            Logger.d("form:" + getFormType() + " onNewActionNode actionNode ----> " + actionNode.toString());
            this.mActionNode = actionNode;
            this.mAppId = actionNode.getAppId();
            this.shouldEndSession = actionNode.isShouldEndSession();
            this.currentMediaBean = actionNode.getMedia();
            this.currentVoiceBean = actionNode.getVoice();
        }
    }

    public String getAppId() {
        return mAppId;
    }

    public boolean isShouldEndSession() {
        return shouldEndSession;
    }

    @Override
    public ActionNode getCurrentActionNode() {
        return mActionNode;
    }

    @Override
    public void onAppPaused() {
        Logger.d("form: " + getFormType() + " onAppPaused");
        currentAppState = APP_STATE.APP_PAUSED;
    }

    @Override
    public void onAppResume() {
        Logger.d("form: " + getFormType() + " onAppResume ");
        AppTypeRecorder.getInstance().storeAppStateManager(this);
        currentAppState = APP_STATE.APP_RESUME;
    }

    @Override
    public void onMediaStart() {
        Logger.d("form: " + getFormType() + " onMediaStart !");
        currentMediaState = MEDIA_STATE.MEDIA_PLAY;
        reporterManager.executeReporter(new MediaReporter(mAppId, MediaReporter.START));
    }

    @Override
    public void onMediaPause(int position) {
        Logger.d("form: " + getFormType() + " onMediaPause ! position : " + position);
        currentMediaState = MEDIA_STATE.MEDIA_PAUSED;
        if (mActionNode != null && currentMediaBean!=null && currentMediaBean.isValid()){
            currentMediaBean.getItem().setOffsetInMilliseconds(position);
        }
    }

    @Override
    public void onMediaResume() {
        Logger.d("form: " + getFormType() + " onMediaResume ! ");
        currentMediaState = MEDIA_STATE.MEDIA_RESUME;
        if (mActionNode != null && currentMediaBean != null && currentMediaBean.isValid()){
            mActionNode.getMedia().getItem().getOffsetInMilliseconds();
        }
    }

    @Override
    public void onMediaStop() {
        Logger.d("form: " + getFormType() + " onMediaStop !");
        currentMediaState = MEDIA_STATE.MEDIA_STOP;
        checkAppState();
    }

    @Override
    public void onMediaError() {
        Logger.d("form: " + getFormType() + " onMediaError !");
        currentMediaState = MEDIA_STATE.MEDIA_ERROR;
        reporterManager.executeReporter(new MediaReporter(mAppId, MediaReporter.ERROR));
        checkAppState();
    }

    @Override
    public void onVoiceStart() {
        Logger.d("form: " + getFormType() + " onVoiceStart !");
        currentVideoState = VOICE_STATE.VOICE_START;
        reporterManager.executeReporter(new VoiceReporter(mAppId, VoiceReporter.START));
    }

    @Override
    public void onVoiceStop() {
        Logger.d("form: " + getFormType() + " onVoiceStop !");
        currentVideoState = VOICE_STATE.VOICE_STOP;
        reporterManager.executeReporter(new MediaReporter(mAppId, VoiceReporter.FINISHED));
        checkAppState();
    }

    private void checkAppState() {
        Logger.d("form: " + getFormType() + "  checkAppState shouldEndSession : " + shouldEndSession + " mediaType : " + currentMediaState + " videoType : " + currentVideoState);

        if (!shouldEndSession){
            Logger.d(" shouldEndSession : " + shouldEndSession);
            return;
        }

        if ((currentMediaState == null || currentMediaState == MEDIA_STATE.MEDIA_STOP || currentMediaState == MEDIA_STATE.MEDIA_ERROR) && (currentVideoState == null || currentVideoState == VOICE_STATE.VOICE_STOP || currentVideoState == VOICE_STATE.VOICE_ERROR)
                && mTaskProcessCallback != null) {
            mTaskProcessCallback.onAllTaskFinished();
            Logger.d("form: " + getFormType() + " voice stop , allTaskFinished ! finish app !");
        }
    }

    @Override
    public void onVoiceCancled() {
        Logger.d("form: " + getFormType() + " onVoiceCancled !");
        currentVideoState = VOICE_STATE.VOICE_CANCLED;
    }

    @Override
    public void onVoiceError() {
        Logger.d("form: " + getFormType() + " onVoiceError !");
        currentVideoState = VOICE_STATE.VOICE_ERROR;
        reporterManager.executeReporter(new VoiceReporter(mAppId, VoiceReporter.FINISHED));
        checkAppState();
    }

    @Override
    public void onEventErrorCallback(String event, int errorCode) {
        Logger.d("form: " + getFormType() + "  onEventErrorCallback " + " event : " + event + " errorCode " + errorCode);
        if (TextUtils.isEmpty(event)) {
            Logger.d(" event is null !");
            return;
        }
        /*if (event.equals(MediaReporter.FINISHED) || event.equals(MediaReporter.NEAR_FINISH)
                || event.equals(VoiceReporter.FINISHED)){
            shouldEndSession = true;
            checkAppState();
        }*/
    }

    @Override
    public void responseCallback(String event, Response response) {
        Logger.d("form: " + getFormType() + " responseCallback event : " + event + " response : " + response);
        ResponseParser.getInstance().parseSendEventResponse(event, response);
    }

    public void finishActivity() {
        if (mTaskProcessCallback != null) {
            Logger.d("form: " + getFormType() + " onExitCallback finishActivity");
            mTaskProcessCallback.onExitCallback();
        }
    }

    public CutAppStateManager.TaskProcessCallback mTaskProcessCallback;

    public void setTaskProcessCallback(CutAppStateManager.TaskProcessCallback taskProcessCallback) {
        this.mTaskProcessCallback = taskProcessCallback;
    }

    public interface TaskProcessCallback {

        void onAllTaskFinished();

        void onExitCallback();
    }

    public abstract String getFormType();

    public enum APP_STATE{
        APP_PAUSED,
        APP_RESUME
    }

    public enum VOICE_STATE {
        VOICE_START,
        VOICE_STOP,
        VOICE_CANCLED,
        VOICE_ERROR
    }

    public enum MEDIA_STATE {
        MEDIA_PLAY,
        MEDIA_PAUSED,
        MEDIA_RESUME,
        MEDIA_STOP,
        MEDIA_ERROR
    }

}
