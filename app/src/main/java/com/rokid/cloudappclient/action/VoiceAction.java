package com.rokid.cloudappclient.action;

import com.rokid.cloudappclient.bean.response.responseinfo.action.voice.VoiceBean;
import com.rokid.cloudappclient.bean.response.responseinfo.action.voice.VoiceItemBean;
import com.rokid.cloudappclient.state.BaseAppStateManager;
import com.rokid.cloudappclient.util.AppTypeRecorder;
import com.rokid.cloudappclient.tts.TTSHelper;
import com.rokid.cloudappclient.util.Logger;

public class VoiceAction extends BaseAction<VoiceBean> {

    private static volatile VoiceAction voiceAction;

    private VoiceBean voiceBean;

    public static VoiceAction getInstance() {
        if (voiceAction == null) {
            synchronized (VoiceAction.class) {
                if (voiceAction == null)
                    voiceAction = new VoiceAction();
            }
        }
        return voiceAction;
    }


    @Override
    public void userStartPlay(VoiceBean actionBean) {
        if (actionBean.isValid()){
            AppTypeRecorder.getInstance().getAppStateManager().setCurrentVoiceState(BaseAppStateManager.VOICE_STATE.VOICE_START);
            AppTypeRecorder.getInstance().getAppStateManager().setUserVoiceControlType(BaseAppStateManager.USER_VOICE_CONTROL_TYPE.VOICE_START);
            this.voiceBean = actionBean;
            //TODO To check whether the voiceBean have confirm, if have confirm speak confirm TTS.
            VoiceItemBean voiceItemBean = actionBean.getItem();
            String ttsContent;
            ttsContent = voiceItemBean.getTts();
            TTSHelper.getInstance().speakTTS(ttsContent);
        }
    }


    @Override
    public synchronized void pausePlay() {
        Logger.d("pause play voice");
        TTSHelper.getInstance().stopTTS();
    }


    @Override
    public synchronized void resumePlay() {
        Logger.d("resume play voiceBean " + voiceBean);
        if (voiceBean != null){
            userStartPlay(voiceBean);
        }
    }

    @Override
    public synchronized void stopPlay() {
        Logger.d("stop play voice");
        voiceBean = null;
        TTSHelper.getInstance().stopTTS();
    }

    @Override
    public synchronized void userPausedPlay() {
        pausePlay();
        AppTypeRecorder.getInstance().getAppStateManager().setUserVoiceControlType(BaseAppStateManager.USER_VOICE_CONTROL_TYPE.VOICE_PAUSE);
    }


    @Override
    public void userResumePlay() {
        resumePlay();
        AppTypeRecorder.getInstance().getAppStateManager().setUserVoiceControlType(BaseAppStateManager.USER_VOICE_CONTROL_TYPE.VOICE_RESUME);
    }

    @Override
    public synchronized void userStopPlay() {
        stopPlay();
        AppTypeRecorder.getInstance().getAppStateManager().setUserVoiceControlType(BaseAppStateManager.USER_VOICE_CONTROL_TYPE.VOICE_STOP);
    }



    @Override
    public void forward() {

    }

    @Override
    public void backward() {

    }

    @Override
    public ACTION_TYPE getActionType() {
        return ACTION_TYPE.VOICE;
    }

}
