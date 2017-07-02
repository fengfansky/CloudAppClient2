package com.rokid.cloudappclient.action;

import com.rokid.cloudappclient.bean.response.responseinfo.action.voice.VoiceBean;
import com.rokid.cloudappclient.bean.response.responseinfo.action.voice.VoiceItemBean;
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
    public void startPlay(VoiceBean actionBean) {
        if (actionBean.isValid()){
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
    public void resumePlay() {
        if (voiceBean != null){
            startPlay(voiceBean);
        }
    }

    @Override
    public synchronized void stopPlay() {
        Logger.d("stopPlay stop play voice");
        TTSHelper.getInstance().stopTTS();
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
