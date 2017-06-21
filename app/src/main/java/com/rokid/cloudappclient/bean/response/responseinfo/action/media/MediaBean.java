package com.rokid.cloudappclient.bean.response.responseinfo.action.media;

import android.text.TextUtils;

import com.rokid.cloudappclient.bean.base.BaseBean;

/**
 * Media is used to play streaming media.
 * Both audio and video are supported. Action, behaviour and media item are defined in media section.
 * <p>
 * Author: xupan.shi
 * Version: V0.1 2017/3/7
 */
public class MediaBean extends BaseBean {

    /**
     * Defines the action of the media player.
     * ONLY TWO actions are supported, which are PLAY and PAUSE.
     */
    public static final String ACTION_PLAY = "PLAY";
    public static final String ACTION_PAUSE = "PAUSE";
    public static final String ACTION_STOP = "STOP";
    public static final String ACTION_RESUME = "RESUME";

    private String action;
    private String behaviour;
    private MediaItemBean item;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getBehaviour() {
        return behaviour;
    }

    public void setBehaviour(String behaviour) {
        this.behaviour = behaviour;
    }

    public MediaItemBean getItem() {
        return item;
    }

    public void setItem(MediaItemBean item) {
        this.item = item;
    }

    public boolean isValid() {
        return isActionValid() && isItemValid();
    }

    public boolean isActionValid() {
        return !TextUtils.isEmpty(action) && (ACTION_PLAY.equals(action) || ACTION_PAUSE.equals(action));
    }

    public boolean isItemValid() {
        return null != item && item.isValid();
    }

}
