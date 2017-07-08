package com.rokid.cloudappclient.action;

import android.net.Uri;
import android.text.TextUtils;

import com.rokid.cloudappclient.RKCloudAppApplication;
import com.rokid.cloudappclient.bean.response.responseinfo.action.media.MediaBean;
import com.rokid.cloudappclient.bean.response.responseinfo.action.media.MediaItemBean;
import com.rokid.cloudappclient.player.RKAudioPlayer;
import com.rokid.cloudappclient.state.BaseAppStateManager;
import com.rokid.cloudappclient.util.AppTypeRecorder;
import com.rokid.cloudappclient.util.Logger;

import tv.danmaku.ijk.media.player.IMediaPlayer;

public class MediaAction extends BaseAction<MediaBean> {

    private static volatile MediaAction mediaAction;

    private RKAudioPlayer rkAudioPlayer;

    private String currentToken;

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
                Logger.d(" onMediaError what : " + what + " extra :" + extra);
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

    public synchronized void userStartPlay(MediaBean mediaBean) {
        if (rkAudioPlayer != null && mediaBean != null) {
            MediaItemBean mediaBeanItem = mediaBean.getItem();
            if (mediaBeanItem == null) {
                Logger.d("start play media mediaBeanItem null!");
                return;
            }

            Logger.d("play mediaBean : " + mediaBean);
            currentToken = mediaBeanItem.getToken();

            String url = mediaBeanItem.getUrl();

            if (TextUtils.isEmpty(url)) {
                Logger.d("media url invalidate!");
                return;
            }

            Logger.d("start play media url : " + url);
            AppTypeRecorder.getInstance().getAppStateManager().setUserMediaControlType(BaseAppStateManager.USER_MEDIA_CONTROL_TYPE.MEDIA_PLAY);
            AppTypeRecorder.getInstance().getAppStateManager().setCurrentMediaState(BaseAppStateManager.MEDIA_STATE.MEDIA_PLAY);
            rkAudioPlayer.setVideoURI(Uri.parse(url));
            rkAudioPlayer.start();
            rkAudioPlayer.seekTo(mediaBeanItem.getOffsetInMilliseconds());
        }
    }

    @Override
    public synchronized void pausePlay() {
        if (rkAudioPlayer != null && rkAudioPlayer.canPause()) {
            rkAudioPlayer.pause();
        }
    }

    @Override
    public synchronized void stopPlay() {
        if (rkAudioPlayer != null && rkAudioPlayer.canPause()) {
            rkAudioPlayer.release(true);
        }
    }

    @Override
    public synchronized void resumePlay() {
        if (rkAudioPlayer != null && !rkAudioPlayer.isPlaying()) {
            rkAudioPlayer.start();
            AppTypeRecorder.getInstance().getAppStateManager().onMediaResume();
        }
    }

    @Override
    public synchronized void userPausedPlay() {
        pausePlay();
        AppTypeRecorder.getInstance().getAppStateManager().setUserMediaControlType(BaseAppStateManager.USER_MEDIA_CONTROL_TYPE.MEDIA_PAUSE);
    }

    @Override
    public synchronized void userStopPlay() {
        stopPlay();
        AppTypeRecorder.getInstance().getAppStateManager().setUserMediaControlType(BaseAppStateManager.USER_MEDIA_CONTROL_TYPE.MEDIA_STOP);
    }

    @Override
    public synchronized void userResumePlay() {
        resumePlay();
        AppTypeRecorder.getInstance().getAppStateManager().setUserMediaControlType(BaseAppStateManager.USER_MEDIA_CONTROL_TYPE.MEDIA_RESUME);
    }

    @Override
    public synchronized void forward() {
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

    @Override
    public synchronized void backward() {
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

    public int getMediaDuration() {
        if (rkAudioPlayer == null) {
            return 0;
        }
        return rkAudioPlayer.getDuration();
    }

    public int getMediaPosition(){
        if (rkAudioPlayer == null){
            return 0;
        }
        return rkAudioPlayer.getCurrentPosition();
    }

    public String getCurrentToken() {
        return currentToken;
    }

    public void releasePlayer() {
        if (rkAudioPlayer != null) {
            rkAudioPlayer.release(true);
            rkAudioPlayer = null;
        }
    }

    @Override
    public ACTION_TYPE getActionType() {
        return ACTION_TYPE.MEDIA;
    }

}
