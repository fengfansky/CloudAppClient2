package com.rokid.cloudappclient.tts;

import android.text.TextUtils;

import com.rokid.logger.Logger;
import com.rokid.tts.BaseTTSHelper;

import rokid.os.RKTTS;
import rokid.os.RKTTSCallback;

/**
 * This is a TTS tools, used to send the TTS, stop the TTS.
 * <p>
 * Author: xupan.shi
 * Version: V0.1 2017/3/10
 */
public class TTSHelper extends BaseTTSHelper{

    private RKTTS mRktts = new RKTTS();


    @Override
    public void speakTTS(String ttsContent) {
        if (TextUtils.isEmpty(ttsContent)) {
            Logger.e("The TTS Content can't be empty!!!");
            return;
        }

        if (ttsId > 0) {
            mRktts.stop(ttsId);
        }

        ttsId = mRktts.speak(ttsContent, rkttsCallback);
//        AppTypeRecorder.getInstance().getAppStateManager().setCurrentVoiceState(BaseAppStateManager.VOICE_STATE.VOICE_START);
        Logger.d(" speak TTS ttiId " + ttsId);
    }

    private RKTTSCallback rkttsCallback = new RKTTSCallback() {
        @Override
        public void onStart(int id) {
            super.onStart(id);
            Logger.i("TTS is onTTSStart - id: " + id);
            if (mVoiceStateCallback != null){
                mVoiceStateCallback.onVoiceStarted();
            }
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
            if (mVoiceStateCallback == null){
                return;
            }
            if (isPaused){
                mVoiceStateCallback.onVoicePaused();
            }else {
                mVoiceStateCallback.onVoiceStopped();
            }
        }

        @Override
        public void onComplete(int id) {
            super.onComplete(id);
            Logger.i("TTS is onComplete - id: " + id);
            ttsId = STOP;
            if (mVoiceStateCallback != null){
                mVoiceStateCallback.onVoiceFinished();
            }
        }

        @Override
        public void onError(int id, int err) {
            super.onError(id, err);
            Logger.i("tts onError - id: " + id + ", error: " + err);
            ttsId = STOP;
            if (mVoiceStateCallback != null){
                mVoiceStateCallback.onVoiceFailed();
            }
        }
    };

    @Override
    public void stopTTS() {
        if (ttsId > 0) {
            isPaused = false;
            mRktts.stop(ttsId);
        }
    }

    @Override
    public void pauseTTS(){
        if (ttsId > 0){
            isPaused = true;
            mRktts.stop(ttsId);
        }
    }

}


