package com.rokid.cloudappclient.bean.response.responseinfo.action.voice;

import android.text.TextUtils;

import com.rokid.cloudappclient.bean.base.BaseBean;

/**
 * Defines the voice interaction of CloudApps, including TTS and Confirmation.
 * <p>
 * Author: xupan.shi
 * Version: V0.1 2017/3/9
 */
public class VoiceBean extends BaseBean {

    /**
     * APPEND - 仅将当前voice加入队列，对当前正在执行的voice和队列已有的voice没有任何影响。
     * REPLACE_ALL - 会将当前正在执行的voice停止，将队列中已有的voice清除，再将当前新的voice加入队列并立
     * 即执行。
     * REPLACE_APPEND - 会将当前正在执行的voice停止，但不会清除队列中已有的voice，将新的voice加入队列     * ，并执行队列。
     * CLEAR - 会立即停止当前所有的voice任务，此策略等同于退出voice，此时voice中的item内容将会被忽略。

     */
    public static final String BEHAVIOUR_APPEND = "APPEND";
    public static final String BEHAVIOUR_REPLACE_ALL = "REPLACE_ALL";
    public static final String BEHAVIOUR_REPLACE_APPEND = "REPLACE_APPEND";
    public static final String BEHAVIOUR_CLEAR = "CLEAR";

    private boolean needEventCallback;
    private String behaviour;
    private VoiceItemBean item;

    public boolean isNeedEventCallback() {
        return needEventCallback;
    }

    public void setNeedEventCallback(boolean needEventCallback) {
        this.needEventCallback = needEventCallback;
    }

    public String getBehaviour() {
        return behaviour;
    }

    public void setBehaviour(String behaviour) {
        this.behaviour = behaviour;
    }

    public VoiceItemBean getItem() {
        return item;
    }

    public void setItem(VoiceItemBean item) {
        this.item = item;
    }

    public boolean isValid() {
        return isBehaviourValid() && isItemValid();
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
