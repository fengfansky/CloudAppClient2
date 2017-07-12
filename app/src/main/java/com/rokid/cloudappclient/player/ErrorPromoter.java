package com.rokid.cloudappclient.player;

import android.content.res.AssetManager;

import com.rokid.cloudappclient.RKCloudAppApplication;
import com.rokid.cloudappclient.util.Logger;

import java.io.IOException;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * Created by fanfeng on 2017/7/12.
 */

public class ErrorPromoter {

    private static volatile ErrorPromoter errorPromoteHelper;
    private RKAudioPlayer rkAudioPlayer;
    private AssetManager assetManager;
    private ErrorPromoteCallback errorPromoteCallback;

    public ErrorPromoter() {
        initRKAudioPlayer();
    }

    private void initRKAudioPlayer() {
        assetManager = RKCloudAppApplication.getInstance().getAssets();
        rkAudioPlayer = new RKAudioPlayer(RKCloudAppApplication.getInstance());
        rkAudioPlayer.setmOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(IMediaPlayer mp) {
                errorPromoteCallback.onPromoteFinished();
            }
        });
    }

    public static ErrorPromoter getInstance() {
        if (errorPromoteHelper == null) {
            synchronized (ErrorPromoter.class) {
                if (errorPromoteHelper == null)
                    errorPromoteHelper = new ErrorPromoter();
            }
        }
        return errorPromoteHelper;
    }

    public void speakErrorPromote(ERROR_TYPE errorType, ErrorPromoteCallback errorPromoteCallback) throws IOException {
        Logger.d(" speakErrorPromote errorType is " + errorType);
        this.errorPromoteCallback = errorPromoteCallback;
        switch (errorType) {
            case MEDIA_TIME_OUT:
                rkAudioPlayer.setAssetVideo(assetManager.openFd("media_timeout.mp3"));
                break;
            case MEDIA_ERROR:
                rkAudioPlayer.setAssetVideo(assetManager.openFd("media_error.mp3"));
                break;
            case DATA_INVALID:
            case TTS_ERROR:
                rkAudioPlayer.setAssetVideo(assetManager.openFd("common_error.mp3"));
                break;
        }
        rkAudioPlayer.start();
    }

    public interface ErrorPromoteCallback {
        void onPromoteFinished();
    }

    public enum ERROR_TYPE {
        DATA_INVALID,
        MEDIA_TIME_OUT,
        MEDIA_ERROR,
        TTS_ERROR
    }

}
