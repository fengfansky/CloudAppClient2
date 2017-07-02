package com.rokid.cloudappclient.bean.response.responseinfo.action;

import android.text.TextUtils;

import com.rokid.cloudappclient.bean.base.BaseBean;
import com.rokid.cloudappclient.util.Logger;

/**
 * Created by fanfeng on 2017/6/24.
 */

public abstract class BaseActionBean extends BaseBean {

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

    public boolean isValid(){
        if (TextUtils.isEmpty(action)){
            Logger.d("action is null !");
            return false;
        }
        //action 为其他操作不需要判断url/tts
        if (ACTION_PLAY.equals(action) && !canPlay()){
            return false;
        }
        return true;
    }

    public abstract boolean canPlay();
}
