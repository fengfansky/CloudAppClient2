package com.rokid.cloudappclient.bean.response.responseinfo.action.media;

import android.text.TextUtils;

/**
 * Defines the media item information which is required by the media player.
 * Here comes the definition.
 * <p>
 * Author: xupan.shi
 * Version: V0.1 2017/3/7
 */
public class MediaItemBean {

    public static final String TYPE_AUDIO = "AUDIO";
    public static final String TYPE_VIDEO = "VIDEO";

    private String token;
    private String type;
    private String url;
    private int offsetInMilliseconds;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getOffsetInMilliseconds() {
        return offsetInMilliseconds;
    }

    public void setOffsetInMilliseconds(int offsetInMilliseconds) {
        this.offsetInMilliseconds = offsetInMilliseconds;
    }

    public boolean isValid() {
        return isTypeValid() && isUrlValid();
    }

    public boolean isTypeValid() {
        return !TextUtils.isEmpty(type) && (TYPE_AUDIO.equals(type) || TYPE_VIDEO.equals(type));
    }

    public boolean isUrlValid() {
        return !TextUtils.isEmpty(url);
    }

}
