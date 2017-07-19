package com.rokid.cloudappclient.bean.response.responseinfo.action.confirm;

import com.rokid.cloudappclient.bean.response.responseinfo.action.voice.ConfirmAttributesBean;

/**
 * Defines the Confirm content for confirm request
 * <p>
 * Author: xupan.shi
 * Version: V0.1 2017/3/9
 */
public class ConfirmBean {

    private String confirmIntent;
    private String confirmSlot;
    private ConfirmAttributesBean optionWords;

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


}


