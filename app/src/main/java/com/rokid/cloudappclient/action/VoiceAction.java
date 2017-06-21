package com.rokid.cloudappclient.action;

import android.text.TextUtils;

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

        //TODO To check whether the voiceBean have confirm, if have confirm speak confirm TTS.
        VoiceItemBean voiceItemBean = actionBean.getItem();
        String ttsContent;
        ttsContent = voiceItemBean.getTts();
        if (TextUtils.isEmpty(ttsContent)) {
            Logger.d("ttsContent is null!");
            return;
        }
        TTSHelper.getInstance().speakTTS(ttsContent);
    }

    @Override
    public synchronized void pauseAction() {
        TTSHelper.getInstance().stopTTS();
    }


    @Override
    public synchronized void stopAction() {
        Logger.d("stop play voice");
        TTSHelper.getInstance().stopTTS();
    }

}
