package com.rokid.cloudappclient.bean.response.responseinfo.action.voice;

import com.rokid.cloudappclient.bean.response.responseinfo.action.BaseActionBean;

/**
 * Defines the voice interaction of CloudApps, including TTS and Confirmation.
 * <p>
 * Author: xupan.shi
 * Version: V0.1 2017/3/9
 */
public class VoiceBean extends BaseActionBean {

    private VoiceItemBean item;

    public VoiceItemBean getItem() {
        return item;
    }

    public void setItem(VoiceItemBean item) {
        this.item = item;
    }

    public boolean isValid() {
        return null != item && item.isValid() && isActionValid();
    }

}
