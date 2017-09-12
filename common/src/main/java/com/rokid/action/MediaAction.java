package com.rokid.action;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.rokid.bean.response.responseinfo.action.media.MediaBean;
import com.rokid.bean.response.responseinfo.action.media.MediaItemBean;
import com.rokid.monitor.BaseCloudStateMonitor;
import com.rokid.logger.Logger;

import java.lang.ref.WeakReference;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.RKAudioPlayer;


public class MediaAction extends BaseAction<MediaBean> {

    private RKAudioPlayer rkAudioPlayer;

    public MediaAction(BaseCloudStateMonitor cloudStateMonitor) {
        super(cloudStateMonitor);
    }

    @Override
    public void registerContext(WeakReference<Context> contextWeakReference) {
        super.registerContext(contextWeakReference);
        initRKAudioPlayer();
    }

    private void initRKAudioPlayer() {
        if (mWeakContext.get() == null){
            Logger.d(" context is null return !");
            return;
        }

        rkAudioPlayer = new RKAudioPlayer(mWeakContext.get());

        rkAudioPlayer.setmOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer mp) {
                cloudStateMonitor.onMediaStarted();
            }
        });

        rkAudioPlayer.setmOnPreparedTimeoutListener(new RKAudioPlayer.OnPreparedTimeoutListener() {
            @Override
            public void onPreparedTimeout() {
                cloudStateMonitor.onPreparedTimeout();
            }
        });
        rkAudioPlayer.setmOnErrorListener(new IMediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(IMediaPlayer mp, int what, int extra) {
                Logger.d(" onMediaFailed what : " + what + " extra :" + extra);
                cloudStateMonitor.onMediaFailed(extra);
                return false;
            }
        });
        rkAudioPlayer.setmOnPausedListener(new IMediaPlayer.OnPausedListener() {
            @Override
            public void onPaused(IMediaPlayer mp) {
                cloudStateMonitor.onMediaPaused((int) mp.getCurrentPosition());
            }
        });
        rkAudioPlayer.setmOnStoppedListener(new RKAudioPlayer.OnStoppedListener() {
            @Override
            public void onStopped() {
                cloudStateMonitor.onMediaStopped();

            }
        });
        rkAudioPlayer.setmOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(IMediaPlayer mp) {
                cloudStateMonitor.onMediaFinished();
            }
        });
    }

    public synchronized void userStartPlay(MediaBean mediaBean) {
        Logger.d(" rkAudioPlayer is null ? " + (rkAudioPlayer == null));
        if (mediaBean == null) {
            Logger.d(" userStartPlay mediaBean is null ");
            return;
        }

        if (rkAudioPlayer == null) {
            initRKAudioPlayer();
        }
        MediaItemBean mediaBeanItem = mediaBean.getItem();
        if (mediaBeanItem == null) {
            Logger.d("start play media mediaBeanItem null!");
            return;
        }

        Logger.d("play mediaBean : " + mediaBean);

        String url = mediaBeanItem.getUrl();
        if (!TextUtils.isEmpty(mediaBeanItem.getToken())) {
            Logger.d("token not null ! token: " + mediaBeanItem.getToken());
            if (url.contains("?")){
                url = url + "&token=" + mediaBeanItem.getToken();
            }else {
                url = url + "?token=" + mediaBeanItem.getToken();
            }
        }else {
            Logger.d("token is null! ");
        }

        if (TextUtils.isEmpty(url)) {
            Logger.d("media url invalidate!");
            return;
        }

        Logger.d("start play media url : " + url);
        cloudStateMonitor.setUserMediaControlType(BaseCloudStateMonitor.USER_MEDIA_CONTROL_TYPE.STARTED);
        cloudStateMonitor.setCurrentMediaState(BaseCloudStateMonitor.MEDIA_STATE.STARTED);
        rkAudioPlayer.setVideoURI(Uri.parse(url));
        rkAudioPlayer.start();
        rkAudioPlayer.seekTo(mediaBeanItem.getOffsetInMilliseconds());
    }

    @Override
    public synchronized void pausePlay() {
        if (rkAudioPlayer != null && rkAudioPlayer.canPause()) {
            rkAudioPlayer.pause();
        }
    }

    @Override
    public synchronized void stopPlay() {
        if (rkAudioPlayer != null) {
            rkAudioPlayer.stop();
        }
    }

    @Override
    public synchronized void resumePlay() {
        if (rkAudioPlayer != null && !rkAudioPlayer.isPlaying()) {
            rkAudioPlayer.start();
            cloudStateMonitor.onMediaResumed();
        }
    }

    @Override
    public synchronized void userPausedPlay() {
        pausePlay();
        cloudStateMonitor.setUserMediaControlType(BaseCloudStateMonitor.USER_MEDIA_CONTROL_TYPE.PAUSED);
    }

    @Override
    public synchronized void userStopPlay() {
        stopPlay();
        cloudStateMonitor.setUserMediaControlType(BaseCloudStateMonitor.USER_MEDIA_CONTROL_TYPE.STOPPED);
    }

    @Override
    public synchronized void userResumePlay() {
        resumePlay();
        cloudStateMonitor.setUserMediaControlType(BaseCloudStateMonitor.USER_MEDIA_CONTROL_TYPE.RESUMED);
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

    public int getMediaPosition() {
        if (rkAudioPlayer == null) {
            return 0;
        }
        return rkAudioPlayer.getCurrentPosition();
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
