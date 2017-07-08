package com.rokid.cloudappclient.state;

import android.text.TextUtils;
import android.util.Log;

import com.rokid.cloudappclient.bean.ActionNode;
import com.rokid.cloudappclient.bean.response.responseinfo.action.ActionBean;
import com.rokid.cloudappclient.action.MediaAction;
import com.rokid.cloudappclient.action.VoiceAction;
import com.rokid.cloudappclient.http.HttpClientWrapper;
import com.rokid.cloudappclient.util.Logger;

/**
 * Created by fanfeng on 2017/6/14.
 */

public class SceneAppStateManager extends BaseAppStateManager {

    public static SceneAppStateManager getInstance() {
        return AppStateManagerHolder.instance;
    }

    private static class AppStateManagerHolder {
        private static final SceneAppStateManager instance = new SceneAppStateManager();
    }

    @Override
    public void checkAppState() {
        Log.d("jiabin", "scene checkAppState -- " + "currentMediaState:" + currentMediaState + " | currentVoiceState:" + currentVoiceState);
        super.checkAppState();
    }

    @Override
    public synchronized void onNewEventActionNode(ActionNode actionNode) {
        Log.d("jiabin", "scene onNewEventActionNode ------");
        super.onNewEventActionNode(actionNode);
    }

    @Override
    public synchronized void onNewIntentActionNode(ActionNode actionNode) {
        Log.d("jiabin", "scene onNewIntentActionNode --- " + "form: " + getFormType() + " | actioNode : " + actionNode);
        Logger.d("form: " + getFormType() + "onNewIntentActionNode actioNode : " + actionNode);
        if (actionNode != null) {
            if (TextUtils.isEmpty(actionNode.getAppId())) {
                Logger.d("new cloudAppId is null !");
                checkAppState();
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
            checkAppState();
        }
    }

    @Override
    public synchronized void onAppPaused() {
        super.onAppPaused();
        MediaAction.getInstance().pausePlay();
        VoiceAction.getInstance().pausePlay();
    }

    @Override
    public synchronized void onAppResume() {
        super.onAppResume();
        Logger.d("scene  onAppResume mediaType: " + currentMediaState + " voiceType : " + currentVoiceState + " userMediaControlType: " + userMediaControlType + " userVoiceControlType: " + userVoiceControlType);

        //应用onResume的时候要考虑到用户上次操作是否是暂停
        if (currentMediaState == MEDIA_STATE.MEDIA_PAUSED && !(userMediaControlType == USER_MEDIA_CONTROL_TYPE.MEDIA_PAUSE)) {
            MediaAction.getInstance().resumePlay();
            Logger.d("scene: onAppResume resume play audio");
        }

        if (currentVoiceState == VOICE_STATE.VOICE_CANCLED && !(userVoiceControlType == USER_VOICE_CONTROL_TYPE.VOICE_PAUSE)) {
            VoiceAction.getInstance().resumePlay();
            Logger.d("scene onAppResume play voice");
        }
    }

    @Override
    public void onAppDestory() {
        super.onAppDestory();
        MediaAction.getInstance().releasePlayer();
        HttpClientWrapper.getInstance().close();
    }

    @Override
    public String getFormType() {
        return ActionBean.FORM_SCENE;
    }
}
