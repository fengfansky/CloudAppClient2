package com.rokid.cloudappclient.state;

import android.text.TextUtils;
import android.util.Log;

import com.rokid.cloudappclient.action.MediaAction;
import com.rokid.cloudappclient.action.VoiceAction;
import com.rokid.cloudappclient.bean.ActionNode;
import com.rokid.cloudappclient.bean.response.responseinfo.action.ActionBean;
import com.rokid.cloudappclient.parser.ResponseParser;
import com.rokid.cloudappclient.reporter.BaseReporter;
import com.rokid.cloudappclient.reporter.ExtraBean;
import com.rokid.cloudappclient.reporter.MediaReporter;
import com.rokid.cloudappclient.reporter.ReporterManager;
import com.rokid.cloudappclient.reporter.VoiceReporter;
import com.rokid.cloudappclient.util.AppTypeRecorder;
import com.rokid.cloudappclient.util.Logger;
import com.squareup.okhttp.Response;
//import com.android.okhttp.Response;

/**
 * Created by fanfeng on 2017/6/16.
 */

public abstract class BaseAppStateManager implements AppStateCallback, MediaStateCallback, VoiceStateCallback, BaseReporter.ReporterResponseCallBack {

    public ActionNode mActionNode;
    public String mAppId;

    //表明当此次返回的action执行完后 CloudAppClient 是否要退出，同时，当 shouldEndSession 为 true 时，CloudAppClient 将会忽略 EventRequests，即在action执行过程中不会产生 EventRequest。
    public boolean shouldEndSession;

    public MEDIA_STATE currentMediaState;
    public VOICE_STATE currentVoiceState;

    public USER_MEDIA_CONTROL_TYPE userMediaControlType;
    public USER_VOICE_CONTROL_TYPE userVoiceControlType;

    public ReporterManager reporterManager = ReporterManager.getInstance();

    public MEDIA_STATE getCurrentMediaState() {
        return currentMediaState;
    }

    public void setCurrentMediaState(MEDIA_STATE currentMediaState) {
        this.currentMediaState = currentMediaState;
    }

    public VOICE_STATE getCurrentVoiceState() {
        return currentVoiceState;
    }

    public void setCurrentVoiceState(VOICE_STATE currentVoiceState) {
        this.currentVoiceState = currentVoiceState;
    }

    @Override
    public synchronized void onNewEventActionNode(ActionNode actionNode) {
        Log.d("jiabin","onNewEventActionNode: " + actionNode);
        Logger.d("form: " + getFormType() + "onNewEventActionNode actioNode : " + actionNode + " currentMediaState: " + currentMediaState + " currentVoiceState " + currentVoiceState);
        if (actionNode != null) {

            if (TextUtils.isEmpty(actionNode.getAppId())) {
                Logger.d("new cloudAppId is null !");
                checkAppState();
                return;
            }

            if (!actionNode.getAppId().equals(mAppId)) {
                Logger.d("onNewEventActionNode the appId is the not the same with lastAppId");
                checkAppState();
                return;
            }

            this.shouldEndSession = actionNode.isShouldEndSession();
            processActionNode(actionNode);
        } else {
            checkAppState();
        }
    }

    public synchronized boolean isShouldEndSession() {
        return shouldEndSession;
    }

    @Override
    public synchronized ActionNode getCurrentActionNode() {
        return mActionNode;
    }

    @Override
    public synchronized void onAppPaused() {
        Logger.d("form: " + getFormType() + " onAppPaused " + " currentMediaState: " + currentMediaState + " currentVoiceState " + currentVoiceState);
    }

    @Override
    public synchronized void onAppResume() {
        AppTypeRecorder.getInstance().storeAppStateManager(this);
        Logger.d("form: " + getFormType() + " onAppResume " + " currentMediaState: " + currentMediaState + " currentVoiceState " + currentVoiceState);
    }

    @Override
    public synchronized void onMediaStart() {
        currentMediaState = MEDIA_STATE.MEDIA_PLAY;
        Logger.d("form: " + getFormType() + " onMediaStart ! " + " currentMediaState: " + currentMediaState + " currentVoiceState " + currentVoiceState);
        if (TextUtils.isEmpty(mAppId)) {
            Logger.d(" appId is null !");
            return;
        }

        reporterManager.executeReporter(new MediaReporter(mAppId, MediaReporter.START, getExtraBean()));
    }

    private String getExtraBean() {
        ExtraBean extraBean = new ExtraBean();
        extraBean.setMedia(new ExtraBean.MediaExtraBean(MediaAction.getInstance().getCurrentToken(), String.valueOf(MediaAction.getInstance().getMediaPosition()), String.valueOf(MediaAction.getInstance().getMediaDuration())));
        Logger.d(" extraBean : " + extraBean.toString());
        return extraBean.toString();
    }

    @Override
    public synchronized void onMediaPause(int position) {
        currentMediaState = MEDIA_STATE.MEDIA_PAUSED;
        Logger.d("form: " + getFormType() + " onMediaPause ! position : " + position + " currentMediaState: " + currentMediaState + " currentVoiceState " + currentVoiceState);
        if (TextUtils.isEmpty(mAppId)) {
            Logger.d(" appId is null !");
            return;
        }
        reporterManager.executeReporter(new MediaReporter(mAppId, MediaReporter.PAUSED, getExtraBean()));
    }

    @Override
    public synchronized void onMediaResume() {
        currentMediaState = MEDIA_STATE.MEDIA_RESUME;
        Logger.d("form: " + getFormType() + " onMediaResume ! " + " currentMediaState: " + currentMediaState + " currentVoiceState " + currentVoiceState);
    }

    @Override
    public synchronized void onMediaStop() {
        currentMediaState = MEDIA_STATE.MEDIA_STOP;
        Logger.d("form: " + getFormType() + " onMediaStop !" + " currentMediaState: " + currentMediaState + " currentVoiceState " + currentVoiceState);
        if (shouldEndSession) {
            checkAppState();
        } else {
            if (TextUtils.isEmpty(mAppId)) {
                Logger.d(" appId is null !");
                return;
            }

            reporterManager.executeReporter(new MediaReporter(mAppId, MediaReporter.FINISHED, getExtraBean()));
        }
    }

    @Override
    public synchronized void onMediaError() {
        currentMediaState = MEDIA_STATE.MEDIA_ERROR;
        Logger.d("form: " + getFormType() + " onMediaError !" + " currentMediaState: " + currentMediaState + " currentVoiceState " + currentVoiceState);
        checkAppState();
    }

    @Override
    public synchronized void onVoiceStart() {
        currentVoiceState = VOICE_STATE.VOICE_START;
        Log.d("jiabin","onVoiceStart");
        Logger.d("form: " + getFormType() + " onVoiceStart !" + " currentMediaState: " + currentMediaState + " currentVoiceState " + currentVoiceState);
        if (TextUtils.isEmpty(mAppId)) {
            Logger.d(" appId is null !");
            return;
        }
        reporterManager.executeReporter(new VoiceReporter(mAppId, VoiceReporter.START));
    }

    @Override
    public synchronized void onVoiceStop() {
        currentVoiceState = VOICE_STATE.VOICE_STOP;
        Logger.d("form: " + getFormType() + " onVoiceStop !" + " currentMediaState: " + currentMediaState + " currentVoiceState " + currentVoiceState);
        if (shouldEndSession) {
            checkAppState();
        } else {
            if (TextUtils.isEmpty(mAppId)) {
                Logger.d(" appId is null !");
                checkAppState();
                return;
            }
            reporterManager.executeReporter(new VoiceReporter(mAppId, VoiceReporter.FINISHED));
        }
    }

    //TODO 区分scene和cut异常处理
    public void checkAppState() {
        Logger.d("form: " + getFormType() + "  checkAppState shouldEndSession : " + shouldEndSession + " mediaType : " + currentMediaState + " videoType : " + currentVoiceState);

        if ((currentMediaState == null || currentMediaState == MEDIA_STATE.MEDIA_STOP || currentMediaState == MEDIA_STATE.MEDIA_ERROR) && (currentVoiceState == null || currentVoiceState == VOICE_STATE.VOICE_STOP || currentVoiceState == VOICE_STATE.VOICE_CANCLED || currentVoiceState == VOICE_STATE.VOICE_ERROR)
                && mTaskProcessCallback != null) {
            mTaskProcessCallback.onAllTaskFinished();
            Logger.d("form: " + getFormType() + " voice stop , allTaskFinished ! finish app !");
        }
    }

    @Override
    public synchronized void onVoiceCancled() {
        currentVoiceState = VOICE_STATE.VOICE_CANCLED;
        Logger.d("form: " + getFormType() + " onVoiceCancled !" + " currentMediaState: " + currentMediaState + " currentVoiceState " + currentVoiceState);
        checkAppState();
    }

    @Override
    public synchronized void onVoiceError() {
        currentVoiceState = VOICE_STATE.VOICE_ERROR;
        Logger.d("form: " + getFormType() + " onVoiceError !" + " currentMediaState: " + currentMediaState + " currentVoiceState " + currentVoiceState);
        checkAppState();
    }

    @Override
    public synchronized void onEventErrorCallback(String event, int errorCode) {
        Logger.e("form: " + getFormType() + "  onEventErrorCallback " + " event : " + event + " errorCode " + errorCode + " currentMediaState: " + currentMediaState + " currentVoiceState " + currentVoiceState);
        checkAppState();
    }


    @Override
    public synchronized void onEventResponseCallback(String event, Response response) {
        Logger.d("form: " + getFormType() + " onEventResponseCallback event : " + event + " response : " + response + " currentMediaState: " + currentMediaState + " currentVoiceState " + currentVoiceState);
        ResponseParser.getInstance().parseSendEventResponse(event, response);
    }

    /**
     * To process real action
     *
     * @param actionNode the validated action
     */
    protected void processActionNode(ActionNode actionNode) {

        if (ActionBean.TYPE_EXIT.equals(actionNode.getActionType())) {
            Logger.d("current response is a INTENT EXIT - Finish Activity");
            finishActivity();
            return;
        }

        if (ActionBean.TYPE_NORMAL.equals(actionNode.getActionType())) {

            if (actionNode.getVoice() != null) {
                VoiceAction.getInstance().processAction(actionNode.getVoice());
            }
            if (actionNode.getMedia() != null) {
                MediaAction.getInstance().processAction(actionNode.getMedia());
            }
        }

    }

    public void finishActivity() {
        if (mTaskProcessCallback != null) {
            Logger.d("form: " + getFormType() + " onExitCallback finishActivity");
            mTaskProcessCallback.onExitCallback();
        }
    }

    public TaskProcessCallback mTaskProcessCallback;

    public void setTaskProcessCallback(TaskProcessCallback taskProcessCallback) {
        this.mTaskProcessCallback = taskProcessCallback;
    }

    public interface TaskProcessCallback {

        void onAllTaskFinished();

        void onExitCallback();
    }

    public abstract String getFormType();

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


    public USER_MEDIA_CONTROL_TYPE getUserMediaControlType() {
        return userMediaControlType;
    }

    public void setUserMediaControlType(USER_MEDIA_CONTROL_TYPE userMediaControlType) {
        this.userMediaControlType = userMediaControlType;
    }

    public USER_VOICE_CONTROL_TYPE getUserVoiceControlType() {
        return userVoiceControlType;
    }

    public void setUserVoiceControlType(USER_VOICE_CONTROL_TYPE userVoiceControlType) {
        this.userVoiceControlType = userVoiceControlType;
    }

    public enum USER_MEDIA_CONTROL_TYPE {
        MEDIA_PLAY,
        MEDIA_PAUSE,
        MEDIA_RESUME,
        MEDIA_STOP,
    }

    public enum USER_VOICE_CONTROL_TYPE {
        VOICE_START,
        VOICE_PAUSE,
        VOICE_RESUME,
        VOICE_STOP
    }

}
