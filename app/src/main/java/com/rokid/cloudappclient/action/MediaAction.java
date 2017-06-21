package com.rokid.cloudappclient.action;

import android.net.Uri;
import android.text.TextUtils;

import com.rokid.cloudappclient.RKCloudAppApplication;
import com.rokid.cloudappclient.bean.response.responseinfo.action.media.MediaBean;
import com.rokid.cloudappclient.bean.response.responseinfo.action.media.MediaItemBean;
import com.rokid.cloudappclient.player.RKAudioPlayer;
import com.rokid.cloudappclient.util.AppTypeRecorder;
import com.rokid.cloudappclient.util.Logger;

import tv.danmaku.ijk.media.player.IMediaPlayer;

public class MediaAction extends BaseAction<MediaBean> {

    private static volatile MediaAction mediaAction;

    private RKAudioPlayer rkAudioPlayer;

    private static final String STREAMING_PLAY = "PLAY";
    private static final String STREAMING_PAUSE = "PAUSE";
    private static final String STREAMING_RESUME = "RESUME";
    private static final String STREAMING_FORWARD = "FORWARD";
    private static final String STREAMING_BACKWARD = "BACKWARD";

    private MediaAction() {
        initRKAudioPlayer();
    }

    private void initRKAudioPlayer() {
        rkAudioPlayer = new RKAudioPlayer(RKCloudAppApplication.getInstance());

        rkAudioPlayer.setmOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer mp) {
                AppTypeRecorder.getInstance().getAppStateManager().onMediaStart();
            }
        });

        rkAudioPlayer.setmOnErrorListener(new IMediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(IMediaPlayer mp, int what, int extra) {
                AppTypeRecorder.getInstance().getAppStateManager().onMediaError();
                return false;
            }
        });

        rkAudioPlayer.setmOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(IMediaPlayer mp) {
                AppTypeRecorder.getInstance().getAppStateManager().onMediaStop();
            }
        });
        rkAudioPlayer.setmOnPausedListener(new IMediaPlayer.OnPausedListener() {
            @Override
            public void onPaused(IMediaPlayer mp) {
                AppTypeRecorder.getInstance().getAppStateManager().onMediaPause((int) mp.getCurrentPosition());
            }
        });

    }

    public static MediaAction getInstance() {
        if (mediaAction == null) {
            synchronized (MediaAction.class) {
                if (mediaAction == null)
                    mediaAction = new MediaAction();
            }
        }
        return mediaAction;
    }

    @Override
    public synchronized void startAction(MediaBean mediaBean) {
        if (mediaBean == null || !mediaBean.isValid()) {
            Logger.d("MediaAction startAction mediaBean invalidate!");
            return;
        }

        String action = mediaBean.getAction();

        Logger.d(" startAction action : " + action);
        switch (action) {
            case STREAMING_PLAY:
                startPlay(mediaBean);
                break;
            case STREAMING_PAUSE:
                pausePlay();
                break;
            case STREAMING_RESUME:
                resumePlay();
                break;
            case STREAMING_FORWARD:
                forward();
                break;
            case STREAMING_BACKWARD:
                backward();
                break;
            default:
                Logger.d(" invalidate action ! " + action);
        }
    }

    @Override
    public synchronized void pauseAction() {
        pausePlay();
    }

    @Override
    public synchronized void stopAction() {
        stopPlay();
    }

    private void startPlay(MediaBean mediaBean) {
        if (rkAudioPlayer != null && mediaBean != null) {
            MediaItemBean mediaBeanItem = mediaBean.getItem();
            if (mediaBeanItem == null) {
                Logger.d("start play media mediaBeanItem null!");
                return;
            }

            String url = mediaBeanItem.getUrl();

            if (TextUtils.isEmpty(url)) {
                Logger.d("media url invalidate!");
                return;
            }

            Logger.d("start play media url : " + url);
            rkAudioPlayer.setVideoURI(Uri.parse(url));
            rkAudioPlayer.seekTo(mediaBeanItem.getOffsetInMilliseconds());
            rkAudioPlayer.start();
        }
    }

    private void pausePlay() {
        if (rkAudioPlayer != null && rkAudioPlayer.canPause()) {
            rkAudioPlayer.pause();
        }
    }

    private void stopPlay() {
        if (rkAudioPlayer != null && rkAudioPlayer.canPause()) {
            rkAudioPlayer.seekTo(rkAudioPlayer.getDuration());
            rkAudioPlayer.pause();
        }
    }

    private void resumePlay() {
        if (rkAudioPlayer != null && !rkAudioPlayer.isPlaying() ) {
            rkAudioPlayer.start();
            AppTypeRecorder.getInstance().getAppStateManager().onMediaResume();
        }
    }

    private void forward() {
        if (rkAudioPlayer != null && !rkAudioPlayer.canSeekForward()) {
            int totalTime = rkAudioPlayer.getDuration();
            int currentTime = rkAudioPlayer.getCurrentPosition();
            int seekTime = currentTime + totalTime / 10;
            if (seekTime > totalTime) {
                seekTime = totalTime;
            }
            rkAudioPlayer.seekTo(seekTime);
        }
    }

    private void backward() {
        if (rkAudioPlayer != null && !rkAudioPlayer.canSeekForward()) {
            int totalTime = rkAudioPlayer.getDuration();
            int currentTime = rkAudioPlayer.getCurrentPosition();
            int seekTime = currentTime - totalTime / 10;
            if (seekTime <= 0) {
                seekTime = 0;
            }
            rkAudioPlayer.seekTo(seekTime);
        }
    }
}
