package com.rokid.cloudappclient.bean.response.responseinfo.action.media;

import android.text.TextUtils;

import com.rokid.cloudappclient.bean.response.responseinfo.action.BaseActionBean;

/**
 * Media is used to play streaming media.
 * Both audio and video are supported. Action, behaviour and media item are defined in media section.
 * <p>
 * Author: xupan.shi
 * Version: V0.1 2017/3/7
 */
public class MediaBean extends BaseActionBean {

    /**
     * Defines the action of the media player.
     * ONLY TWO actions are supported, which are PLAY and PAUSE.
     */

    private MediaItemBean item;

    public MediaItemBean getItem() {
        return item;
    }

    public void setItem(MediaItemBean item) {
        this.item = item;
    }

    @Override
    public boolean canPlay() {
        return item != null && !TextUtils.isEmpty(item.getUrl());
    }
}
