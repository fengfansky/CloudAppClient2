package com.rokid.action;

import com.rokid.bean.response.responseinfo.action.voice.VoiceBean;
import com.rokid.bean.response.responseinfo.action.voice.VoiceItemBean;
import com.rokid.monitor.BaseCloudStateMonitor;
import com.rokid.logger.Logger;
import com.rokid.tts.TTSUtils;

public class VoiceAction extends BaseAction<VoiceBean> {

    private VoiceBean voiceBean;

    public VoiceAction(BaseCloudStateMonitor cloudStateMonitor) {
        super(cloudStateMonitor);
    }

    @Override
    public void userStartPlay(VoiceBean actionBean) {
        if (actionBean.isValid()) {
            cloudStateMonitor.setCurrentVoiceState(BaseCloudStateMonitor.VOICE_STATE.STARTED);
            cloudStateMonitor.setUserVoiceControlType(BaseCloudStateMonitor.USER_VOICE_CONTROL_TYPE.STARTED);
            this.voiceBean = actionBean;
            VoiceItemBean voiceItemBean = actionBean.getItem();
            String ttsContent;
            ttsContent = voiceItemBean.getTts();
            TTSUtils.getInstance().getTtsHelper().speakTTS(ttsContent);
        }
    }

    @Override
    public synchronized void pausePlay() {
        Logger.d("pause play voice");
        TTSUtils.getInstance().getTtsHelper().pauseTTS();
    }

    @Override
    public synchronized void resumePlay() {
        Logger.d("resume play voiceBean " + voiceBean);
        if (voiceBean != null) {
            userStartPlay(voiceBean);
        }
    }

    @Override
    public synchronized void stopPlay() {
        Logger.d("stop play voice");
        voiceBean = null;
        TTSUtils.getInstance().getTtsHelper().stopTTS();
    }

    @Override
    public synchronized void userPausedPlay() {
        pausePlay();
        cloudStateMonitor.setUserVoiceControlType(BaseCloudStateMonitor.USER_VOICE_CONTROL_TYPE.PAUSED);
    }

    @Override
    public void userResumePlay() {
        resumePlay();
        cloudStateMonitor.setUserVoiceControlType(BaseCloudStateMonitor.USER_VOICE_CONTROL_TYPE.RESUMED);
    }

    @Override
    public synchronized void userStopPlay() {
        stopPlay();
        cloudStateMonitor.setUserVoiceControlType(BaseCloudStateMonitor.USER_VOICE_CONTROL_TYPE.STOPPED);
    }

    @Override
    public void getStatus() {

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

    public VoiceBean getVoiceBean() {
        return voiceBean;
    }
}
