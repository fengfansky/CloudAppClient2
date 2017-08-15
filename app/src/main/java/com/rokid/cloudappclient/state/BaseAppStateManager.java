package com.rokid.cloudappclient.state;

import android.text.TextUtils;

import com.rokid.cloudappclient.action.MediaAction;
import com.rokid.cloudappclient.action.VoiceAction;
import com.rokid.cloudappclient.bean.ActionNode;
import com.rokid.cloudappclient.bean.response.responseinfo.action.ActionBean;
import com.rokid.cloudappclient.player.ErrorPromoter;
import com.rokid.cloudappclient.parser.ResponseParser;
import com.rokid.cloudappclient.player.RKAudioPlayer;
import com.rokid.cloudappclient.reporter.BaseReporter;
import com.rokid.cloudappclient.reporter.ExtraBean;
import com.rokid.cloudappclient.reporter.MediaReporter;
import com.rokid.cloudappclient.reporter.ReporterManager;
import com.rokid.cloudappclient.reporter.VoiceReporter;
import com.rokid.cloudappclient.util.Logger;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.lang.ref.WeakReference;
//import com.android.okhttp.Response;

/**
 * Created by fanfeng on 2017/6/16.
 */

public abstract class BaseAppStateManager implements AppStateCallback, MediaStateCallback, VoiceStateCallback, BaseReporter.ReporterResponseCallBack {

    private static final String SIREN_TYPE_CONFIRM = "CONFIRM";
    private static final String SIREN_TYPE_PICKUP = "PICKUP";

    public ActionNode mActionNode;
    public String mAppId;

    //表明当此次返回的action执行完后 CloudAppClient 是否要退出，同时，当 shouldEndSession 为 true 时，CloudAppClient 将会忽略 EventRequests，即在action执行过程中不会产生 EventRequest。
    public boolean shouldEndSession;

    public PROMOTE_STATE promoteState;

    public MEDIA_STATE currentMediaState;
    public VOICE_STATE currentVoiceState;

    public USER_MEDIA_CONTROL_TYPE userMediaControlType;
    public USER_VOICE_CONTROL_TYPE userVoiceControlType;

    public ReporterManager reporterManager = ReporterManager.getInstance();


    @Override
    public synchronized void onNewIntentActionNode(ActionNode actionNode) {
        Logger.d("form: " + getFormType() + "onNewIntentActionNode actioNode : " + actionNode);
        if (actionNode != null) {
            if (TextUtils.isEmpty(actionNode.getAppId())) {
                Logger.d("new cloudAppId is null !");
                checkStateValid();
                return;
            }

            if (!actionNode.getAppId().equals(mAppId)) {
                Logger.d("onNewEventActionNode the appId is the not the same with lastAppId");
                MediaAction.getInstance().stopPlay();
                VoiceAction.getInstance().stopPlay();
                this.currentMediaState = null;
                this.currentVoiceState = null;
            }
            this.mActionNode = actionNode;
            this.mAppId = actionNode.getAppId();
            this.shouldEndSession = actionNode.isShouldEndSession();
            processActionNode(actionNode);

        } else {
            checkStateValid();
        }
    }

    @Override
    public synchronized void onNewEventActionNode(ActionNode actionNode) {
        Logger.d("form: " + getFormType() + "onNewEventActionNode actioNode : " + actionNode + " currentMediaState: " + currentMediaState + " currentVoiceState " + currentVoiceState);
        if (actionNode != null) {

            if (TextUtils.isEmpty(actionNode.getAppId())) {
                Logger.d("new cloudAppId is null !");
                checkStateValid();
                return;
            }

            if (!actionNode.getAppId().equals(mAppId)) {
                Logger.d("onNewEventActionNode the appId is the not the same with lastAppId");
                checkStateValid();
                return;
            }

            this.shouldEndSession = actionNode.isShouldEndSession();
            processActionNode(actionNode);
        } else {
            checkStateValid();
        }
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
    public synchronized void onAppDestory() {
        Logger.d("form: " + getFormType() + " onAppDestory " + " currentMediaState: " + currentMediaState + " currentVoiceState: " + currentVoiceState);
    }

    @Override
    public synchronized void onMediaStart() {
        currentMediaState = MEDIA_STATE.MEDIA_START;
        Logger.d("form: " + getFormType() + " onMediaStart ! " + " currentMediaState: " + currentMediaState + " currentVoiceState " + currentVoiceState);

        sendMediaReporter(MediaReporter.START);
    }

    @Override
    public synchronized void onMediaPause(int position) {
        currentMediaState = MEDIA_STATE.MEDIA_PAUSED;
        Logger.d("form: " + getFormType() + " onMediaPause ! position : " + position + " currentMediaState: " + currentMediaState + " currentVoiceState " + currentVoiceState);
        sendMediaReporter(MediaReporter.PAUSED);
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
            checkStateValid();
        } else {
            if (TextUtils.isEmpty(mAppId)) {
                Logger.d(" appId is null !");
                return;
            }

            sendMediaReporter(MediaReporter.FINISHED);
        }
    }

    @Override
    public synchronized void onMediaError(int errorCode) {
        currentMediaState = MEDIA_STATE.MEDIA_ERROR;
        Logger.d("form: " + getFormType() + " onMediaError ! errorCode : " + errorCode + " currentMediaState: " + currentMediaState + " currentVoiceState " + currentVoiceState);
        if (errorCode == RKAudioPlayer.MEDIA_ERROR_TIME_OUT) {
            promoteErrorInfo(ErrorPromoter.ERROR_TYPE.MEDIA_TIME_OUT);
        } else {
            promoteErrorInfo(ErrorPromoter.ERROR_TYPE.MEDIA_ERROR);
        }
    }

    @Override
    public synchronized void onVoiceStart() {
        currentVoiceState = VOICE_STATE.VOICE_START;
        Logger.d("form: " + getFormType() + " onVoiceStart !" + " currentMediaState: " + currentMediaState + " currentVoiceState " + currentVoiceState);
        sendVoiceReporter(VoiceReporter.START);
    }

    @Override
    public synchronized void onVoiceStop() {
        currentVoiceState = VOICE_STATE.VOICE_STOP;
        Logger.d("form: " + getFormType() + " onVoiceStop !" + " currentMediaState: " + currentMediaState + " currentVoiceState " + currentVoiceState);
        if (shouldEndSession) {
            checkStateValid();
        } else {
            sendVoiceReporter(VoiceReporter.FINISHED);
        }
    }

    @Override
    public void onVoicePaused() {
        currentVoiceState = VOICE_STATE.VOICE_PAUSED;
        Logger.d("form: " + getFormType() + " onVoiceCancled !" + " currentMediaState: " + currentMediaState + " currentVoiceState " + currentVoiceState);
    }

    @Override
    public synchronized void onVoiceCancled() {
        currentVoiceState = VOICE_STATE.VOICE_CANCLED;
        Logger.d("form: " + getFormType() + " onVoiceCancled !" + " currentMediaState: " + currentMediaState + " currentVoiceState " + currentVoiceState);
        checkStateValid();
    }

    @Override
    public synchronized void onVoiceError() {
        currentVoiceState = VOICE_STATE.VOICE_ERROR;
        Logger.d("form: " + getFormType() + " onVoiceError !" + " currentMediaState: " + currentMediaState + " currentVoiceState " + currentVoiceState);
        promoteErrorInfo(ErrorPromoter.ERROR_TYPE.TTS_ERROR);
    }

    @Override
    public synchronized void onEventErrorCallback(String event, ERROR_CODE errorCode) {
        Logger.e(" event error call back !!!");
        Logger.e("form: " + getFormType() + "  onEventErrorCallback " + " event : " + event + " errorCode " + errorCode + " currentMediaState: " + currentMediaState + " currentVoiceState " + currentVoiceState);
        checkStateValid();
//        promoteErrorInfo(ErrorPromoter.ERROR_TYPE.NO_TASK_PROCESS);
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
                if (actionNode.getConfirmBean() != null && mTaskProcessCallback != null && mTaskProcessCallback.get() != null) {
                    //confirm 打开拾音，默认6秒
                    mTaskProcessCallback.get().openSiren(SIREN_TYPE_CONFIRM, true, 6000);
                }
            }
            if (actionNode.getMedia() != null) {
                MediaAction.getInstance().processAction(actionNode.getMedia());
            }
        }
    }

    private void sendVoiceReporter(String action) {
        if (TextUtils.isEmpty(mAppId)) {
            Logger.d(" appId is null !");
            checkStateValid();
            return;
        }
        if (mActionNode == null || mActionNode.getVoice() == null) {
            Logger.d(" mActionNode or voice is null ! ");
            checkStateValid();
            return;
        }
        if (mActionNode.getVoice().isDisableEvent()) {
            Logger.d("SendEventRequest disableEvent closed!");
            checkStateValid();
            return;
        }

        ExtraBean extraBean = new ExtraBean();
        if (mActionNode.getVoice().getItem() == null) {
            reporterManager.executeReporter(new VoiceReporter(mAppId, action));
        } else {
            Logger.d(" extraBean : " + extraBean.toString());
            extraBean.setVoice(new ExtraBean.VoiceExtraBean(mActionNode.getVoice().getItem().getItemId()));
            reporterManager.executeReporter(new VoiceReporter(mAppId, action, extraBean.toString()));
        }
    }

    private void sendMediaReporter(String action) {
        if (TextUtils.isEmpty(mAppId)) {
            Logger.d(" appId is null !");
            checkStateValid();
            return;
        }
        if (mActionNode == null || mActionNode.getMedia() == null) {
            Logger.d("mActionNode or media is null!");
            checkStateValid();
            return;
        }

        if (mActionNode.getMedia().isDisableEvent()) {
            Logger.d("SendEventRequest disableEvent closed!");
            checkStateValid();
            return;
        }
        ExtraBean extraBean = new ExtraBean();

        if (mActionNode.getMedia().getItem() == null) {
            extraBean.setMedia(new ExtraBean.MediaExtraBean(String.valueOf(MediaAction.getInstance().getMediaPosition()), String.valueOf(MediaAction.getInstance().getMediaDuration())));
        } else {
            extraBean.setMedia(new ExtraBean.MediaExtraBean(mActionNode.getMedia().getItem().getItemId(), mActionNode.getMedia().getItem().getToken(), String.valueOf(MediaAction.getInstance().getMediaPosition()), String.valueOf(MediaAction.getInstance().getMediaDuration())));
        }
        Logger.d(" extraBean : " + extraBean.toString());

        reporterManager.executeReporter(new MediaReporter(mAppId, action, extraBean.toString()));
    }

    private void promoteErrorInfo(ErrorPromoter.ERROR_TYPE errorType) {
        Logger.d(" promoteErrorInfo isStateInvalid : " + isStateInvalid());
        if (isStateInvalid()) {
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
                            mTaskProcessCallback.get().onAllTaskFinished();
                        }
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void checkStateValid() {
        if (isStateInvalid() && mTaskProcessCallback != null && mTaskProcessCallback.get() != null) {
            Logger.d("checkStateValid form: " + getFormType() + " voice stop , checkAllTaskIsFinished ! finish app !");
            mTaskProcessCallback.get().onInvalidateState();
            if (mActionNode.getPickup() != null) {
                Logger.d("pickUp : " + mActionNode.getPickup().toString());
                mTaskProcessCallback.get().openSiren(SIREN_TYPE_PICKUP, true, mActionNode.getPickup().getDurationInMilliseconds());
            }
        }
    }

    private boolean isStateInvalid() {
        Logger.d("form: " + getFormType() + " isStateInvalid shouldEndSession : " + shouldEndSession + " mediaType : " + currentMediaState + " videoType : " + currentVoiceState +
                " promoteState : " + promoteState);
        return (currentMediaState == null || currentMediaState == MEDIA_STATE.MEDIA_STOP || currentMediaState == MEDIA_STATE.MEDIA_ERROR) && (currentVoiceState == null || currentVoiceState == VOICE_STATE.VOICE_STOP || currentVoiceState == VOICE_STATE.VOICE_CANCLED || currentVoiceState == VOICE_STATE.VOICE_ERROR) && (promoteState == null || promoteState == PROMOTE_STATE.FINISHED);
    }

    public void finishActivity() {
        if (mTaskProcessCallback != null && mTaskProcessCallback.get() != null) {
            Logger.d("form: " + getFormType() + " onExitCallback finishActivity");
            mTaskProcessCallback.get().onExitCallback();
        }
    }

    public WeakReference<TaskProcessCallback> mTaskProcessCallback;

    public void setTaskProcessCallback(TaskProcessCallback taskProcessCallback) {
        this.mTaskProcessCallback = new WeakReference<>(taskProcessCallback);
    }

    public interface TaskProcessCallback {

        void openSiren(String type, boolean pickupEnable, int durationInMilliseconds);

        void onInvalidateState();

        void onAllTaskFinished();

        void onExitCallback();
    }

    public abstract String getFormType();

    public enum PROMOTE_STATE {
        STARTED,
        FINISHED
    }

    public enum VOICE_STATE {
        VOICE_START,
        VOICE_PAUSED,
        VOICE_STOP,
        VOICE_CANCLED,
        VOICE_ERROR
    }

    public enum MEDIA_STATE {
        MEDIA_START,
        MEDIA_PAUSED,
        MEDIA_RESUME,
        MEDIA_STOP,
        MEDIA_ERROR
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

    public enum USER_MEDIA_CONTROL_TYPE {
        MEDIA_START,
        MEDIA_PAUSE,
        MEDIA_RESUME,
        MEDIA_STOP
    }

    public enum USER_VOICE_CONTROL_TYPE {
        VOICE_START,
        VOICE_PAUSE,
        VOICE_RESUME,
        VOICE_STOP
    }

}
