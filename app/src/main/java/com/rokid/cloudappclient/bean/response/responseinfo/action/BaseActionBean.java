package com.rokid.cloudappclient.bean.response.responseinfo.action;

import android.text.TextUtils;

import com.rokid.cloudappclient.bean.base.BaseBean;

/**
 * Created by fanfeng on 2017/6/24.
 */

public class BaseActionBean extends BaseBean {

    public static final String ACTION_PLAY = "PLAY";
    public static final String ACTION_PAUSE = "PAUSE";
    public static final String ACTION_RESUME = "RESUME";
    public static final String ACTION_STOP = "STOP";
    public static final String ACTION_FORWARD = "FORWARD";
    public static final String ACTION_BACKWARD = "BACKWARD";

    public String action;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }


    public boolean isActionValid() {
        return !TextUtils.isEmpty(action);
    }
}
