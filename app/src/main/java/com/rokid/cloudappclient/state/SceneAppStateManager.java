package com.rokid.cloudappclient.state;

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
    public void onAppPaused() {
        super.onAppPaused();
        MediaAction.getInstance().pauseAction();
        VoiceAction.getInstance().pauseAction();
    }

    @Override
    public void onAppResume() {
        super.onAppResume();
        Logger.d("scene  onAppResume mediaType: " + currentMediaState + " voiceType : " + currentVideoState);
        if (currentMediaState == MEDIA_STATE.MEDIA_PAUSED){
            MediaAction.getInstance().startAction(currentMediaBean);
            Logger.d("scene: onAppResume resume play audio");
        }
        if (currentVideoState == VOICE_STATE.VOICE_STOP){
            VoiceAction.getInstance().startAction(currentVoiceBean);
            Logger.d("scene onAppResume play voice");
        }

    }

    @Override
    public String getFormType() {
        return ActionBean.FORM_SCENE;
    }
}
