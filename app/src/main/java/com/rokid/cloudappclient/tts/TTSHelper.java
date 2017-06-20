package com.rokid.cloudappclient.tts;

import android.os.RemoteException;
import android.text.TextUtils;

import com.rokid.cloudappclient.reporter.BaseReporter;
import com.rokid.cloudappclient.reporter.ReporterManager;
import com.rokid.cloudappclient.reporter.VoiceReporter;
import com.rokid.cloudappclient.util.AppTypeRecorder;
import com.rokid.cloudappclient.util.Logger;

import java.util.concurrent.ConcurrentLinkedQueue;

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
    private ConcurrentLinkedQueue<Node> bufferQueue = new ConcurrentLinkedQueue<>();
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

    public int speakTTSError(String ttsContent) {
        if (TextUtils.isEmpty(ttsContent)) {
            Logger.e("The TTS Content can't be empty!!!");

            return STOP;
        }

        ttsId = mRktts.speak(ttsContent, new RKTTSCallback() {
            @Override
            public void onStart(int id) {
                super.onStart(id);
                Logger.i("TTS is onTTSStart - id: " + id);
            }

            @Override
            public void onCancel(int id) {
                super.onCancel(id);
                Logger.i("TTS is onStop - id: " + id + ", current id: " + ttsId);

                if (id != ttsId) {
                    Logger.i("The new tts is already speaking, previous tts stop should not ttsCallback");
                    return;
                }

                ttsId = STOP;
                speakTTSFromBufferQueue();
            }

            @Override
            public void onComplete(int id) {
                super.onComplete(id);
                Logger.i("TTS is onComplete - id: " + id);

                ttsId = STOP;
                speakTTSFromBufferQueue();
            }

            @Override
            public void onError(int id, int err) {
                super.onError(id, err);
                Logger.i("tts onError - id: " + id + ", error: " + err);

                ttsId = STOP;
                speakTTSFromBufferQueue();
            }
        });
        return STOP;
    }

    public void speakTTS(String ttsContent) {
        if (TextUtils.isEmpty(ttsContent)) {
            Logger.e("The TTS Content can't be empty!!!");
            return;
        }

        Logger.d(" speak TTS ttiId " + ttsId);
        if (ttsId > 0) {
            mRktts.stop(ttsId);
        }

        ttsId = mRktts.speak(ttsContent, rkttsCallback);
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
            Logger.i("TTS is onStop - id: " + id + ", current id: " + ttsId);

            AppTypeRecorder.getInstance().getAppStateManager().onVoiceCancled();
            speakTTSFromBufferQueue();
        }

        @Override
        public void onComplete(int id) {
            super.onComplete(id);
            Logger.i("TTS is onComplete - id: " + id);
            AppTypeRecorder.getInstance().getAppStateManager().onVoiceStop();
            speakTTSFromBufferQueue();
        }

        @Override
        public void onError(int id, int err) {
            super.onError(id, err);
            Logger.i("tts onError - id: " + id + ", error: " + err);
            ttsId = STOP;
            AppTypeRecorder.getInstance().getAppStateManager().onVoiceError();
            speakTTSFromBufferQueue();
        }
    };

    private void speakTTS(String ttsContent,RKTTSCallback ttsCallback) throws RemoteException {
        if (TextUtils.isEmpty(ttsContent)) {
            Logger.e("The TTS Content can't be empty!!!");
            ttsCallback.onError(STOP, STOP);
            return;
        }
        Logger.i("start to speakTTS - ttsContent: " + ttsContent);

        if (mRktts == null) {
            Logger.i("TTSService is unbind, push the data to the buffer queue!!!");
            bufferQueue.add(new Node(ttsContent, ttsCallback));
            return;
        }

        if (ttsId > 0) {
            mRktts.stop(ttsId);
        }
        ttsId = mRktts.speak(ttsContent, ttsCallback);
    }

    public void stopTTS() {
        if (ttsId > 0) {
            mRktts.stop(ttsId);
        }
    }

    private void speakTTSFromBufferQueue() {
        if (bufferQueue.size() < 1) {
            Logger.i("Buffer queue is empty, don't play the TTS");
            return;
        }

        try {
            Logger.i("Start speak TTS from bufferQueue");
            Node node = bufferQueue.poll();
            speakTTS(node.ttsContent, node.ttsCallback);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private static class Node {
        String ttsContent;
        RKTTSCallback ttsCallback;

        public Node(String ttsContent, RKTTSCallback ttsCallback) {
            this.ttsContent = ttsContent;
            this.ttsCallback = ttsCallback;
        }
    }



}
