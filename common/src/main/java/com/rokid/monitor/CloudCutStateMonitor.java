package com.rokid.monitor;

import com.rokid.bean.response.responseinfo.action.ActionBean;
import com.rokid.logger.Logger;

/**
 * Created by fanfeng on 2017/6/14.
 */

public class CloudCutStateMonitor extends BaseCloudStateMonitor {

    public static CloudCutStateMonitor getInstance() {
        return AppStateManagerHolder.instance;
    }

    private static class AppStateManagerHolder {
        private static final CloudCutStateMonitor instance = new CloudCutStateMonitor();
    }

    @Override
    public synchronized void onPause() {
        super.onPause();
        Logger.d(" cut : stop tts and media , finishActivity");
        voiceAction.stopPlay();
        mediaAction.stopPlay();
    }

    @Override
    public String getFormType() {
        return ActionBean.FORM_CUT;
    }

    // Cut应用拾音打开：TTS停止播放，Media暂停播放
    @Override
    public void onSirenOpened() {
        voiceAction.stopPlay();
        mediaAction.pausePlay();
    }

    // Cut应用拾音关闭：TTS不变，Media恢复播放
    @Override
    public void onSirenClosed() {
        mediaAction.resumePlay();
    }
}
