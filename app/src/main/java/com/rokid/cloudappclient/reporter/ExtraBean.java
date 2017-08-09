package com.rokid.cloudappclient.reporter;

import com.rokid.cloudappclient.bean.base.BaseBean;

/**
 * Created by fanfeng on 2017/6/29.
 */

public class ExtraBean extends BaseBean{

    private MediaExtraBean media;
    private VoiceExtraBean voice;

    public MediaExtraBean getMedia() {
        return media;
    }

    public void setMedia(MediaExtraBean media) {
        this.media = media;
    }

    public VoiceExtraBean getVoice() {
        return voice;
    }

    public void setVoice(VoiceExtraBean voice) {
        this.voice = voice;
    }

    public static class MediaExtraBean extends BaseExtraBean{

        public MediaExtraBean(String progress, String duration) {
            super(progress, duration);
        }

        public MediaExtraBean(String token, String progress, String duration) {
            super(token, progress, duration);
        }

        public MediaExtraBean(String itemId, String token, String progress, String duration) {
            super(itemId, token, progress, duration);
        }
    }


    public static class VoiceExtraBean extends BaseExtraBean{

        public VoiceExtraBean(String itemId) {
            super(itemId);
        }

        public VoiceExtraBean(String token, String progress, String duration) {
            super(token, progress, duration);
        }

        public VoiceExtraBean(String itemId, String token, String progress, String duration) {
            super(itemId, token, progress, duration);
        }
    }

    public static abstract class BaseExtraBean{
        private String itemId;
        private String token;
        private String progress;
        private String duration;

        public BaseExtraBean(String itemId) {
            this.itemId = itemId;
        }

        public BaseExtraBean(String progress, String duration) {
            this.progress = progress;
            this.duration = duration;
        }

        public BaseExtraBean(String token, String progress, String duration) {
            this.token = token;
            this.progress = progress;
            this.duration = duration;
        }

        public BaseExtraBean(String itemId, String token, String progress, String duration) {
            this.itemId = itemId;
            this.token = token;
            this.progress = progress;
            this.duration = duration;
        }

        public String getItemId() {
            return itemId;
        }

        public void setItemId(String itemId) {
            this.itemId = itemId;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getProgress() {
            return progress;
        }

        public void setProgress(String progress) {
            this.progress = progress;
        }

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }
    }


}
