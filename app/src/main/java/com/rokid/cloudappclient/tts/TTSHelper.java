package com.rokid.cloudappclient.tts;

import android.text.TextUtils;

import com.rokid.cloudappclient.state.BaseAppStateManager;
import com.rokid.cloudappclient.util.AppTypeRecorder;
import com.rokid.cloudappclient.util.Logger;

import rokid.os.RKTTS;
import rokid.os.RKTTSCallback;

/**
 * This is a TTS tools, used to send the TTS, stop the TTS.
 * <p>
 * Author: xupan.shi
 * Version: V0.1 2017/3/10
 */
public class TTSHelper {
    private static final int STOP = -1;
    private volatile static TTSHelper instance;
    private RKTTS mRktts = new RKTTS();
    private volatile int ttsId = STOP;
    public boolean isPaused = false;
    public static TTSHelper getInstance() {
        if (null == instance) {
            synchronized (TTSHelper.class) {
                if (null == instance) {
                    instance = new TTSHelper();
                }
            }
        }

        return instance;
    }

    public void speakTTS(String ttsContent) {
        if (TextUtils.isEmpty(ttsContent)) {
            Logger.e("The TTS Content can't be empty!!!");
            return;
        }

        if (ttsId > 0) {
            mRktts.stop(ttsId);
        }

        ttsId = mRktts.speak(ttsContent, rkttsCallback);
        AppTypeRecorder.getInstance().getAppStateManager().setCurrentVoiceState(BaseAppStateManager.VOICE_STATE.VOICE_START);
        Logger.d(" speak TTS ttiId " + ttsId);
    }

    private RKTTSCallback rkttsCallback = new RKTTSCallback() {
        @Override
        public void onStart(int id) {
            super.onStart(id);
            Logger.i("TTS is onTTSStart - id: " + id);
            AppTypeRecorder.getInstance().getAppStateManager().onVoiceStart();
        }

        @Override
        public void onCancel(int id) {
            super.onCancel(id);
            Logger.i("TTS is onCancel - id: " + id + ", current id: " + ttsId + " isPaused : " + isPaused);
            if (id != ttsId) {
                Logger.i("The new tts is already speaking, previous tts stop should not ttsCallback");
                return;
            }
            ttsId = STOP;
            if (isPaused){
                AppTypeRecorder.getInstance().getAppStateManager().onVoicePaused();
            }else {
                AppTypeRecorder.getInstance().getAppStateManager().onVoiceCancled();
            }
        }

        @Override
        public void onComplete(int id) {
            super.onComplete(id);
            Logger.i("TTS is onComplete - id: " + id);
            ttsId = STOP;
            AppTypeRecorder.getInstance().getAppStateManager().onVoiceStop();
        }

        @Override
        public void onError(int id, int err) {
            super.onError(id, err);
            Logger.i("tts onError - id: " + id + ", error: " + err);
            ttsId = STOP;
            AppTypeRecorder.getInstance().getAppStateManager().onVoiceError();
        }
    };

    public void stopTTS() {
        if (ttsId > 0) {
            isPaused = false;
            mRktts.stop(ttsId);
        }
    }

    public void pauseTTS(){
        if (ttsId > 0){
            isPaused = true;
            mRktts.stop(ttsId);
        }
    }

}
