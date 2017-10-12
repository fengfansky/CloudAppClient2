package com.rokid.monitor;

import android.content.Context;
import android.text.TextUtils;

import com.rokid.bean.ActionNode;
import com.rokid.bean.response.responseinfo.action.ActionBean;
import com.rokid.action.MediaAction;
import com.rokid.action.VoiceAction;
import com.rokid.http.HttpClientWrapper;
import com.rokid.logger.Logger;
import com.rokid.parser.ResponseParser;
import com.rokid.reporter.BaseReporter;
import com.rokid.reporter.DialogReporter;
import com.rokid.reporter.ExtraBean;
import com.rokid.reporter.MediaReporter;
import com.rokid.reporter.ReporterManager;
import com.rokid.reporter.VoiceReporter;
import com.rokid.tts.BaseTTSHelper;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.lang.ref.WeakReference;

import tv.danmaku.ijk.media.player.promoter.ErrorPromoter;
//import com.android.okhttp.Response;

/**
 * Created by fanfeng on 2017/6/16.
 */

public abstract class BaseCloudStateMonitor implements CloudStateCallback, MediaStateCallback, BaseTTSHelper.VoiceStateCallback, BaseReporter.ReporterResponseCallback {

    public static final String SIREN_TYPE_CONFIRM = "CONFIRM";
    public static final String SIREN_TYPE_PICKUP = "PICKUP";

    protected ActionNode mActionNode;
    protected String mAppId;

    //表明当此次返回的action执行完后 CloudAppClient 是否要退出，同时，当 shouldEndSession 为 true 时，CloudAppClient 将会忽略 EventRequests，即在action执行过程中不会产生 EventRequest。
    protected boolean shouldEndSession;

    protected PROMOTE_STATE promoteState;

    protected MEDIA_STATE currentMediaState;
    protected VOICE_STATE currentVoiceState;

    protected USER_MEDIA_CONTROL_TYPE userMediaControlType;
    protected USER_VOICE_CONTROL_TYPE userVoiceControlType;

    protected ExtraBean.MediaExtraBean mediaExtraBean;
    protected ExtraBean.VoiceExtraBean voiceExtraBean;

    public MediaAction mediaAction;
    public VoiceAction voiceAction;

    private String mCloudStatus;
    private boolean isDestroy;

    private ReporterManager reporterManager = ReporterManager.getInstance();

    public BaseCloudStateMonitor() {
        mediaAction = new MediaAction(this);
        voiceAction = new VoiceAction(this);
    }

    public BaseCloudStateMonitor registerContext(WeakReference<Context> contextWeakReference) {
        if (contextWeakReference != null) {
            this.voiceAction.registerContext(contextWeakReference);
            this.mediaAction.registerContext(contextWeakReference);
        }
        return this;
    }

    public void setCurrentMediaState(MEDIA_STATE currentMediaState) {
        this.currentMediaState = currentMediaState;
    }

    public void setCurrentVoiceState(VOICE_STATE currentVoiceState) {
        this.currentVoiceState = currentVoiceState;
    }

    public void setUserMediaControlType(USER_MEDIA_CONTROL_TYPE userMediaControlType) {
        this.userMediaControlType = userMediaControlType;
    }


    public void setUserVoiceControlType(USER_VOICE_CONTROL_TYPE userVoiceControlType) {
        this.userVoiceControlType = userVoiceControlType;
    }

    public String getmAppId() {
        return mAppId;
    }

    public ExtraBean.MediaExtraBean getMediaExtraBean() {
        return mediaExtraBean;
    }

    public ExtraBean.VoiceExtraBean getVoiceExtraBean() {
        return voiceExtraBean;
    }

    @Override
    public synchronized void onNewIntentActionNode(ActionNode actionNode) {
        Logger.d("form: " + getFormType() + " onNewIntentActionNode actionNode : " + actionNode);
        if (actionNode != null) {
            if (TextUtils.isEmpty(actionNode.getAppId())) {
                Logger.d("new cloudAppId is null !");
                return;
            }

            if (!actionNode.getAppId().equals(mAppId)) {
                Logger.d("onNewIntent the appId is the not the same with lastAppId");
                mediaAction.stopPlay();
                voiceAction.stopPlay();
                this.currentMediaState = null;
                this.currentVoiceState = null;
            }
            this.mActionNode = actionNode;
            this.mAppId = actionNode.getAppId();
            this.shouldEndSession = actionNode.isShouldEndSession();
            processActionNode(actionNode);

        } else {
            promoteErrorInfo(ErrorPromoter.ERROR_TYPE.DATA_INVALID);
        }
    }

    @Override
    public synchronized void onNewEventActionNode(ActionNode actionNode) {
        Logger.d("form: " + getFormType() + " onNewEventActionNode actionNode : " + actionNode + " currentMediaState: " + currentMediaState + " currentVoiceState " + currentVoiceState);
        if (actionNode != null) {

            if (TextUtils.isEmpty(actionNode.getAppId())) {
                Logger.d("new cloudAppId is null !");
                return;
            }

            if (!actionNode.getAppId().equals(mAppId)) {
                Logger.d("onNewEventActionNode the appId is the not the same with lastAppId");
                return;
            }

            this.mActionNode = actionNode;
            this.mAppId = actionNode.getAppId();
            this.shouldEndSession = actionNode.isShouldEndSession();
            processActionNode(actionNode);
        } else {
            checkRunningState();
        }
    }

    @Override
    public void onCreate() {
        Logger.d("form: " + getFormType() + " onAppCreate " + " currentMediaState: " + currentMediaState + " currentVoiceState " + currentVoiceState);
        isDestroy = false;
    }

    @Override
    public synchronized void onPause() {
        Logger.d("form: " + getFormType() + " onAppPaused " + " currentMediaState: " + currentMediaState + " currentVoiceState " + currentVoiceState);
    }

    @Override
    public synchronized void onResume() {
        Logger.d("form: " + getFormType() + " onAppResume " + " currentMediaState: " + currentMediaState + " currentVoiceState " + currentVoiceState);
        isDestroy = false;
    }

    @Override
    public synchronized void onDestroy() {
        Logger.d("form: " + getFormType() + " onAppDestroy " + " currentMediaState: " + currentMediaState + " currentVoiceState: " + currentVoiceState);
        isDestroy = true;
        mediaAction.releasePlayer();
        HttpClientWrapper.getInstance().close();
    }

    @Override
    public synchronized void onMediaStarted() {
        currentMediaState = MEDIA_STATE.STARTED;
        Logger.d("form: " + getFormType() + " onMediaStart ! " + " currentMediaState: " + currentMediaState + " currentVoiceState " + currentVoiceState);

        sendMediaReporter(MediaReporter.STARTED);
    }

    @Override
    public void onTruckTimeout() {
        Logger.d("form: " + getFormType() + " onMediaStart ! " + " currentMediaState: " + currentMediaState + " currentVoiceState " + currentVoiceState);
        sendMediaReporter(MediaReporter.TIMEOUT);
    }

    @Override
    public synchronized void onMediaPaused(int position) {
        currentMediaState = MEDIA_STATE.PAUSED;
        Logger.d("form: " + getFormType() + " onMediaPause ! position : " + position + " currentMediaState: " + currentMediaState + " currentVoiceState " + currentVoiceState);
        sendMediaReporter(MediaReporter.PAUSED);
    }

    @Override
    public synchronized void onMediaResumed() {
        currentMediaState = MEDIA_STATE.RESUMED;
        Logger.d("form: " + getFormType() + " onMediaResume ! " + " currentMediaState: " + currentMediaState + " currentVoiceState " + currentVoiceState);
        sendMediaReporter(MediaReporter.STARTED);
    }

    @Override
    public synchronized void onMediaStopped() {
        currentMediaState = MEDIA_STATE.STOPPED;
        Logger.d("form: " + getFormType() + " onMediaComplete !" + " currentMediaState: " + currentMediaState + " currentVoiceState " + currentVoiceState);
    }

    @Override
    public synchronized void onMediaFinished() {
        currentMediaState = MEDIA_STATE.FINISHED;
        Logger.d("form: " + getFormType() + " onMediaFinished !" + " currentMediaState: " + currentMediaState + " currentVoiceState " + currentVoiceState);
        if (shouldEndSession) {
            checkRunningState();
        }else {
            sendMediaReporter(MediaReporter.FINISHED);
        }

    }

    @Override
    public synchronized void onMediaFailed(int errorCode) {
        currentMediaState = MEDIA_STATE.ERROR;
        Logger.d("form: " + getFormType() + " onMediaFailed ! errorCode : " + errorCode + " currentMediaState: " + currentMediaState + " currentVoiceState " + currentVoiceState);
        sendMediaReporter(MediaReporter.FAILED);
    }

    @Override
    public synchronized void onVoiceStarted() {
        currentVoiceState = VOICE_STATE.STARTED;
        Logger.d("form: " + getFormType() + " onVoiceStarted !" + " currentMediaState: " + currentMediaState + " currentVoiceState " + currentVoiceState);
        sendVoiceReporter(VoiceReporter.STARTED);
    }

    @Override
    public synchronized void onVoicePaused() {
        currentVoiceState = VOICE_STATE.PAUSED;
        Logger.d("form: " + getFormType() + " onVoiceCanceled !" + " currentMediaState: " + currentMediaState + " currentVoiceState " + currentVoiceState);
    }

    @Override
    public synchronized void onVoiceStopped() {
        currentVoiceState = VOICE_STATE.STOPPED;
        Logger.d("form: " + getFormType() + " onVoiceStopped !" + " currentMediaState: " + currentMediaState + " currentVoiceState " + currentVoiceState);
    }

    @Override
    public synchronized void onVoiceFailed() {
        currentVoiceState = VOICE_STATE.ERROR;
        Logger.d("form: " + getFormType() + " onVoiceError !" + " currentMediaState: " + currentMediaState + " currentVoiceState " + currentVoiceState);
//        promoteErrorInfo(ErrorPromoter.ERROR_TYPE.TTS_ERROR);
        sendVoiceReporter(VoiceReporter.FAILED);
    }

    @Override
    public synchronized void onVoiceFinished() {
        currentVoiceState = VOICE_STATE.FINISHED;
        Logger.d("form: " + getFormType() + " onVoiceFinished !" + " currentMediaState: " + currentMediaState + " currentVoiceState " + currentVoiceState);

        if (shouldEndSession) {
            checkRunningState();
        }else {
            sendVoiceReporter(VoiceReporter.FINISHED);
        }
    }

    @Override
    public synchronized void onEventErrorCallback(String event, ERROR_CODE errorCode) {
        Logger.e(" event error call back !!!");
        Logger.e("form: " + getFormType() + "  onEventErrorCallback " + " event : " + event + " errorCode " + errorCode + " currentMediaState: " + currentMediaState + " currentVoiceState " + currentVoiceState);

        //加载音频文件出错或者卡顿超过5s的事件超时时的处理
        if (errorCode == ERROR_CODE.ERROR_CONNECTION_TIMEOUT ) {
            if (MediaReporter.TIMEOUT == event || MediaReporter.FAILED == event) {
                if (isStateInvalid()) {
                    promoteErrorInfo(ErrorPromoter.ERROR_TYPE.NET_BAD);
                    return;
                }
            }
        }

        checkRunningState();
    }


    @Override
    public synchronized void onEventResponseCallback(String event, Response response) {
        Logger.d("form: " + getFormType() + " onEventResponseCallback event : " + event + " response : " + response + " currentMediaState: " + currentMediaState + " currentVoiceState " + currentVoiceState);
        reporterCount--;
        Logger.d(" onEventResponseCallback reporterCount: " + reporterCount);
        ResponseParser.getInstance().parseSendEventResponse(event, response, this);
    }

    /**
     * To process real action
     *
     * @param actionNode the validated action
     */
    protected void processActionNode(ActionNode actionNode) {

        if (ActionBean.TYPE_EXIT.equals(actionNode.getActionType())) {
            Logger.d("current response is a INTENT EXIT - Finish Activity");
            exitApp();
            return;
        }

        if (ActionBean.TYPE_NORMAL.equals(actionNode.getActionType())) {

            if (actionNode.getVoice() != null) {
                voiceAction.processAction(actionNode.getVoice());
            }
            if (actionNode.getMedia() != null) {
                mediaAction.processAction(actionNode.getMedia());
            }
        }
    }

    public String getCloudStatus() {

        final ExtraBean extraBean = getStateExtraBean();
        if (extraBean == null) return null;

        setCloudStatus("\"" + mAppId + "\":" +
                extraBean.toString());

        return mCloudStatus;
    }

    private ExtraBean getStateExtraBean() {
        if (isDestroy) {
            Logger.d(getFormType() + " app not exit !");
            return null;
        }

        if (TextUtils.isEmpty(mAppId)) {
            Logger.d(" appId is null !");
            return null;
        }

        if (mActionNode == null) {
            Logger.d(" mActionNode is null ! ");
            return null;
        }

        final ExtraBean extraBean = new ExtraBean();

        String mediaState;

        if (currentMediaState == MEDIA_STATE.STARTED || currentMediaState == MEDIA_STATE.RESUMED) {
            mediaState = "PLAYING";
        } else if (currentMediaState == MEDIA_STATE.PAUSED) {
            mediaState = "PAUSED";
        } else {
            mediaState = "IDLE";
        }

        if (mActionNode.getMedia() != null) {
            if (mActionNode.getMedia().getItem() == null) {
                extraBean.setMedia(mediaExtraBean = new ExtraBean.MediaExtraBean(mediaState, String.valueOf(mediaAction.getMediaPosition()), String.valueOf(mediaAction.getMediaDuration())));
            } else {
                extraBean.setMedia(mediaExtraBean = new ExtraBean.MediaExtraBean(mediaState, mActionNode.getMedia().getItem().getItemId(), mActionNode.getMedia().getItem().getToken(), String.valueOf(mediaAction.getMediaPosition()), String.valueOf(mediaAction.getMediaDuration())));
            }
        } else {
            extraBean.setMedia(mediaExtraBean = new ExtraBean.MediaExtraBean(mediaState));
        }

        String voiceState;

        if (currentVoiceState == VOICE_STATE.STARTED) {
            voiceState = "PLAYING";
        }/* else if (currentVoiceState == VOICE_STATE.PAUSED) {
            voiceState = "PAUSED";
        }*/ else {
            voiceState = "IDLE";
        }

        if (mActionNode.getVoice() != null) {
            if (mActionNode.getVoice().getItem() == null) {
                extraBean.setVoice(voiceExtraBean = new ExtraBean.VoiceExtraBean(voiceState));
            } else {
                extraBean.setVoice(voiceExtraBean = new ExtraBean.VoiceExtraBean(voiceState, mActionNode.getVoice().getItem().getItemId()));
            }
        } else {
            extraBean.setVoice(voiceExtraBean = new ExtraBean.VoiceExtraBean(voiceState));
        }
        return extraBean;
    }

    public void setCloudStatus(String mCloudStatus) {
        this.mCloudStatus = mCloudStatus;
    }

    public void sendVoiceReporter(String action) {

        /*if (shouldEndSession) {
            Logger.d("shouldEndSession true don't sendVoiceReporter");
            return;
        }*/

        if (mActionNode == null || mActionNode.getVoice() == null) {
            Logger.d(" mActionNode or voice is null ! ");
            checkRunningState();
            return;
        }
        if (mActionNode.getVoice().isDisableEvent()) {
            Logger.d("SendEventRequest disableEvent true!");
            checkRunningState();
            return;
        }

        if (TextUtils.isEmpty(mAppId)) {
            Logger.d(" appId is null !");
            checkRunningState();
            return;
        }

        String voiceState;
        if (currentVoiceState == VOICE_STATE.STARTED) {
            voiceState = "PLAYING";
        } else if (currentVoiceState == VOICE_STATE.PAUSED) {
            voiceState = "PAUSED";
        } else {
            voiceState = "IDLE";
        }

        ExtraBean extraBean = new ExtraBean();

        if (mActionNode.getVoice().getItem() == null) {
            extraBean.setVoice(voiceExtraBean = new ExtraBean.VoiceExtraBean(voiceState));
        } else {
            extraBean.setVoice(voiceExtraBean = new ExtraBean.VoiceExtraBean(voiceState, mActionNode.getVoice().getItem().getItemId()));
        }
        Logger.d("sendVoiceReporter extraBean : " + extraBean.toString());

        reporterManager.executeReporter(new VoiceReporter(mAppId, action, extraBean.toString(), this));

        reporterCount++;
        Logger.d("sendVoiceReporter action : " + action + " reporterCount : " + reporterCount);

    }

    private volatile int reporterCount;

    public void sendMediaReporter(String action) {

        /*if (shouldEndSession) {
            Logger.d("shouldEndSession true don't sendMediaReporter");
            return;
        }*/

        if (mActionNode == null || mActionNode.getMedia() == null) {
            Logger.d("mActionNode or media is null!");
            checkRunningState();
            return;
        }

        if (mActionNode.getMedia().isDisableEvent()) {
            Logger.d("SendEventRequest disableEvent closed!");
            checkRunningState();
            return;
        }

        if (TextUtils.isEmpty(mAppId)) {
            Logger.d(" appId is null !");
            checkRunningState();
            return;
        }

        String mediaState;
        if (currentMediaState == MEDIA_STATE.STARTED) {
            mediaState = "PLAYING";
        } else if (currentMediaState == MEDIA_STATE.PAUSED) {
            mediaState = "PAUSED";
        } else {
            mediaState = "IDLE";
        }

        ExtraBean extraBean = new ExtraBean();

        if (mediaAction.getMediaBeanItem() == null) {
            extraBean.setMedia(mediaExtraBean = new ExtraBean.MediaExtraBean(mediaState, String.valueOf(mediaAction.getMediaPosition()), String.valueOf(mediaAction.getMediaDuration())));
        } else {
            extraBean.setMedia(mediaExtraBean = new ExtraBean.MediaExtraBean(mediaState,mediaAction.getMediaBeanItem().getItemId(), mediaAction.getMediaBeanItem().getToken(), String.valueOf(mediaAction.getMediaPosition()), String.valueOf(mediaAction.getMediaDuration())));
        }
        Logger.d("sendMediaReporter extraBean : " + extraBean.toString());

        reporterManager.executeReporter(new MediaReporter(mAppId, action, extraBean.toString(), this));

        reporterCount++;
        Logger.d(" sendMediaReporter action " + action +  " reporterCount : " + reporterCount);
    }


    public void promoteErrorInfo(ErrorPromoter.ERROR_TYPE errorType) {
        Logger.d(" promoteErrorInfo isStateInvalid : " + isStateInvalid() + " errorType : " + errorType);

        if (!isStateInvalid()) {
            Logger.d("promoteErrorInfo app is running , don't finish app !");
            return;
        }

        try {
            ErrorPromoter.getInstance().speakErrorPromote(errorType, new ErrorPromoter.ErrorPromoteCallback() {
                @Override
                public void onPromoteStarted() {
                    Logger.d(" onPromoteStarted!");
                    promoteState = PROMOTE_STATE.STARTED;
                }

                @Override
                public void onPromoteFinished() {
                    if (mTaskProcessCallback != null && mTaskProcessCallback.get() != null) {
                        Logger.d(" onPromoteFinished !");
                        promoteState = PROMOTE_STATE.FINISHED;
                        mTaskProcessCallback.get().onTaskFinished();
                        if (shouldEndSession){
                            mTaskProcessCallback.get().exitSessionToAppEngine();
                        }
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void checkRunningState() {

        if (!isStateInvalid()) {
            Logger.d("onVoiceFinished app is running , don't finish app !");
            return;
        }

        Logger.d(" actionFinished reporterCount : " + reporterCount);

        if (reporterCount != 0) {
            Logger.d(" reporter sending , don't finish app !");
            return;
        }

        Logger.d("checkStateValid form: " + getFormType() + " action finished  ! finish app !");

        mTaskProcessCallback.get().onTaskFinished();
        if (shouldEndSession){
            mTaskProcessCallback.get().exitSessionToAppEngine();
        }

        openSiren();
    }


    private void openSiren() {
        if (mActionNode != null && mActionNode.getConfirmBean() != null) {
            //confirm 打开拾音，默认6秒
            Logger.d("confirm : " + mActionNode.getConfirmBean());
            mTaskProcessCallback.get().openSiren(SIREN_TYPE_CONFIRM, true, 6000);
            return;
        }
        if (mActionNode != null && mActionNode.getPickup() != null) {
            Logger.d("pickUp : " + mActionNode.getPickup().toString());
            mTaskProcessCallback.get().openSiren(SIREN_TYPE_PICKUP, true, mActionNode.getPickup().getDurationInMilliseconds());
        }
    }

    private boolean isStateInvalid() {
        Logger.d("form: " + getFormType() + " isStateInvalid shouldEndSession : " + shouldEndSession + " mediaType : " + currentMediaState + " VoiceType : " + currentVoiceState +
                " promoteState : " + promoteState);
        return (currentMediaState == null || currentMediaState == MEDIA_STATE.FINISHED || currentMediaState == MEDIA_STATE.STOPPED || currentMediaState == MEDIA_STATE.ERROR) && (currentVoiceState == null || currentVoiceState == VOICE_STATE.STOPPED || currentVoiceState == VOICE_STATE.FINISHED || currentVoiceState == VOICE_STATE.ERROR) && (promoteState == null || promoteState == PROMOTE_STATE.FINISHED);
    }

    public void exitApp() {
        if (mTaskProcessCallback != null && mTaskProcessCallback.get() != null) {
            Logger.d("form: " + getFormType() + " onExitCallback finishActivity");
            mTaskProcessCallback.get().onExitCallback();
            mTaskProcessCallback.get().exitSessionToAppEngine();
        }
    }

    public WeakReference<TaskProcessCallback> mTaskProcessCallback;

    public void setTaskProcessCallback(WeakReference<TaskProcessCallback> taskProcessCallbackWeakReference) {
        this.mTaskProcessCallback = taskProcessCallbackWeakReference;
    }

    public interface TaskProcessCallback {

        void openSiren(String type, boolean pickupEnable, int durationInMilliseconds);

        void exitSessionToAppEngine();

        void onTaskFinished( );

        void onExitCallback();
    }

    public abstract String getFormType();

    public abstract void onSirenOpened();

    public void onSirenClosed(){
        ReporterManager.getInstance().executeReporter(new DialogReporter("", DialogReporter.DIALOG_DISMISS, this));

    }

    public enum PROMOTE_STATE {
        STARTED,
        FINISHED
    }

    public enum VOICE_STATE {
        STARTED,
        PAUSED,
        STOPPED,
        FINISHED,
        ERROR
    }

    public enum MEDIA_STATE {
        STARTED,
        PAUSED,
        RESUMED,
        STOPPED,
        FINISHED,
        ERROR
    }

    public enum USER_VOICE_CONTROL_TYPE {
        STARTED,
        PAUSED,
        RESUMED,
        STOPPED
    }

    public enum USER_MEDIA_CONTROL_TYPE {
        STARTED,
        PAUSED,
        RESUMED,
        STOPPED
    }

}
