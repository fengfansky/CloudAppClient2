package com.rokid.cloudappclient.state;

import android.text.TextUtils;

import com.rokid.cloudappclient.bean.ActionNode;
import com.rokid.cloudappclient.bean.response.responseinfo.action.ActionBean;
import com.rokid.cloudappclient.action.MediaAction;
import com.rokid.cloudappclient.action.VoiceAction;
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
    public synchronized void onNewIntentActionNode(ActionNode actionNode) {
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
        Logger.d("scene  onAppResume mediaType: " + currentMediaState + " voiceType : " + currentVoiceState);
        if (currentMediaState == MEDIA_STATE.MEDIA_PAUSED) {
            MediaAction.getInstance().resumePlay();
            Logger.d("scene: onAppResume resume play audio");
        }
        if (currentVoiceState == VOICE_STATE.VOICE_CANCLED) {
            VoiceAction.getInstance().resumePlay();
            Logger.d("scene onAppResume play voice");
        }

    }

    @Override
    public String getFormType() {
        return ActionBean.FORM_SCENE;
    }
}
