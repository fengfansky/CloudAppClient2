package com.rokid.cloudappclient.state;

import com.rokid.cloudappclient.bean.ActionNode;
import com.rokid.cloudappclient.bean.response.responseinfo.action.ActionBean;
import com.rokid.cloudappclient.action.MediaAction;
import com.rokid.cloudappclient.action.VoiceAction;
import com.rokid.cloudappclient.util.Logger;

/**
 * Created by fanfeng on 2017/6/14.
 */

public class SceneAppStateManager extends BaseAppStateManager{

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
            this.mActionNode = actionNode;
            this.mAppId = actionNode.getAppId();
            this.shouldEndSession = actionNode.isShouldEndSession();
            this.currentMediaState = null;
            this.currentVoiceState = null;
            this.currentMediaBean = actionNode.getMedia();
            this.currentVoiceBean = actionNode.getVoice();
            processActionNode(actionNode);
        }else {
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
        if (currentMediaState == MEDIA_STATE.MEDIA_PAUSED){
            MediaAction.getInstance().processAction(currentMediaBean);
            Logger.d("scene: onAppResume resume play audio");
        }
        if (currentVoiceState == VOICE_STATE.VOICE_CANCLED){
            VoiceAction.getInstance().processAction(currentVoiceBean);
            Logger.d("scene onAppResume play voice");
        }

    }

    @Override
    public String getFormType() {
        return ActionBean.FORM_SCENE;
    }
}
