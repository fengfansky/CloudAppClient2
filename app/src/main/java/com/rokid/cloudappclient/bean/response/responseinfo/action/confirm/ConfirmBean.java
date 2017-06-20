package com.rokid.cloudappclient.bean.response.responseinfo.action.confirm;

import android.text.TextUtils;

import com.rokid.cloudappclient.bean.response.responseinfo.action.voice.ConfirmAttributesBean;

/**
 * Defines the Confirm content for confirm request
 * <p>
 * Author: xupan.shi
 * Version: V0.1 2017/3/9
 */
public class ConfirmBean {

    private String tts;
    private String confirmIntent;
    private String confirmSlot;
    private ConfirmAttributesBean confirmAttributes;

    public String getTts() {
        return tts;
    }

    public void setTts(String tts) {
        this.tts = tts;
    }

    public String getConfirmIntent() {
        return confirmIntent;
    }

    public void setConfirmIntent(String confirmIntent) {
        this.confirmIntent = confirmIntent;
    }

    public String getConfirmSlot() {
        return confirmSlot;
    }

    public void setConfirmSlot(String confirmSlot) {
        this.confirmSlot = confirmSlot;
    }

    public ConfirmAttributesBean getConfirmAttributes() {
        return confirmAttributes;
    }

    public void setConfirmAttributes(ConfirmAttributesBean confirmAttributes) {
        this.confirmAttributes = confirmAttributes;
    }

    public boolean isValid() {
        return !TextUtils.isEmpty(tts);
    }

}
