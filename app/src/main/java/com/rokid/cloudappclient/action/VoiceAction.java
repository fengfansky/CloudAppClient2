package com.rokid.cloudappclient.action;

import android.text.TextUtils;

import com.rokid.cloudappclient.bean.response.responseinfo.action.voice.VoiceItemBean;
import com.rokid.cloudappclient.bean.transfer.TransferVoiceBean;
import com.rokid.cloudappclient.tts.TTSHelper;
import com.rokid.cloudappclient.util.Logger;

public class VoiceAction extends BaseAction<TransferVoiceBean> {

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
    public synchronized void startAction(TransferVoiceBean transfer) {

        if (null == transfer || !transfer.isValid()) {
            Logger.d("Now have a voice in running or TransferVoiceBean is empty.");
            return;
        }
        mTransfer = transfer;
        Logger.d(" startAction " + mTransfer.toString());

        Logger.d("start play voice");

        //TODO To check whether the voiceBean have confirm, if have confirm speak confirm TTS.
        VoiceItemBean voiceItemBean = transfer.getVoiceBean().getItem();
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
    public synchronized void resumeAction() {
        startAction(mTransfer);
        Logger.d("resume start play voice");
    }

    @Override
    public synchronized void stopAction() {
        super.stopAction();
        Logger.d("stop play voice");
        TTSHelper.getInstance().stopTTS();
    }

}
