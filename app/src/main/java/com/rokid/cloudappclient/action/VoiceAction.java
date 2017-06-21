package com.rokid.cloudappclient.action;

import com.rokid.cloudappclient.bean.response.responseinfo.action.voice.VoiceBean;
import com.rokid.cloudappclient.bean.response.responseinfo.action.voice.VoiceItemBean;
import com.rokid.cloudappclient.tts.TTSHelper;
import com.rokid.cloudappclient.util.Logger;

public class VoiceAction extends BaseAction<VoiceBean> {

    private static volatile VoiceAction voiceAction;

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
    public synchronized void startAction(VoiceBean actionBean) {
        Logger.d("start play voice");

        if (actionBean == null || !actionBean.isValid()){
            Logger.d(" startAction voiceBean invalid! ");
            return;
        }

        //TODO To check whether the voiceBean have confirm, if have confirm speak confirm TTS.
        VoiceItemBean voiceItemBean = actionBean.getItem();
        String ttsContent;
        ttsContent = voiceItemBean.getTts();
        TTSHelper.getInstance().speakTTS(ttsContent);
    }

    @Override
    public synchronized void pauseAction() {

    }


    @Override
    public synchronized void stopAction() {
        Logger.d("stop play voice");
        TTSHelper.getInstance().stopTTS();
    }

}
