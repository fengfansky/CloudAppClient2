package com.rokid.cloudappclient.bean.response.responseinfo.action.pickup;

/**
 * Created by fanfeng on 2017/8/7.
 */

public class PickupBean {

    private boolean enable;
    private int durationInMilliseconds;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public int getDurationInMilliseconds() {
        return durationInMilliseconds;
    }

    public void setDurationInMilliseconds(int durationInMilliseconds) {
        this.durationInMilliseconds = durationInMilliseconds;
    }
}
