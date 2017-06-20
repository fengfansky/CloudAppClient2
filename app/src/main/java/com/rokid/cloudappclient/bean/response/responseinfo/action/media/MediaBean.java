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
    public static final String ACTION_RESUME = "RESUME";

    public static final String BEHAVIOUR_APPEND = "APPEND";
    public static final String BEHAVIOUR_REPLACE_ALL = "REPLACE_ALL";
    public static final String BEHAVIOUR_REPLACE_APPEND = "REPLACE_APPEND";
    public static final String BEHAVIOUR_CLEAR = "CLEAR";

    private boolean needEventCallback;
    private String action;
    private String behaviour;
    private MediaItemBean item;

    public boolean isNeedEventCallback() {
        return needEventCallback;
    }

    public void setNeedEventCallback(boolean needEventCallback) {
        this.needEventCallback = needEventCallback;
    }

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
        return isActionValid() && isBehaviourValid() && isItemValid();
    }

    public boolean isActionValid() {
        return !TextUtils.isEmpty(action) && (ACTION_PLAY.equals(action) || ACTION_PAUSE.equals(action));
    }

    public boolean isBehaviourValid() {
        return !TextUtils.isEmpty(behaviour)
                && (BEHAVIOUR_APPEND.equals(behaviour) || BEHAVIOUR_REPLACE_ALL.equals(behaviour)
                || BEHAVIOUR_REPLACE_APPEND.equals(behaviour) || BEHAVIOUR_CLEAR.equals(behaviour));
    }

    public boolean isItemValid() {
        return null != item && item.isValid();
    }

}
