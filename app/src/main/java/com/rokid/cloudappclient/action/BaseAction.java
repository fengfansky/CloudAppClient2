package com.rokid.cloudappclient.action;

import android.text.TextUtils;
import com.rokid.cloudappclient.bean.response.responseinfo.action.BaseActionBean;
import com.rokid.cloudappclient.util.Logger;

/**
 * Created by fanfeng on 2017/4/20.
 */

public abstract class BaseAction<T extends BaseActionBean> {

    public void processAction(T actionBean){
        Logger.d(getActionType() + " processAction + actionBean : " + actionBean);
        if (actionBean == null ) {
            Logger.d(getActionType() + " processAction actionBean is  null! " );
            return;
        }

        String action = actionBean.getAction();

        if (TextUtils.isEmpty(action)){
            Logger.d(getActionType() + " action is null!");
            return;
        }

        switch (action) {
            case BaseActionBean.ACTION_PLAY:
                userStartPlay(actionBean);
                break;
            case BaseActionBean.ACTION_PAUSE:
                userPausedPlay();
                break;
            case BaseActionBean.ACTION_RESUME:
                userResumePlay();
                break;
            case BaseActionBean.ACTION_STOP:
                userStopPlay();
                break;
            case BaseActionBean.ACTION_FORWARD:
                forward();
                break;
            case BaseActionBean.ACTION_BACKWARD:
                backward();
                break;
            default:
                Logger.d(" invalidate action ! " + action);
        }
    }

    public abstract void userStartPlay(T actionBean);

    public abstract void userPausedPlay();

    public abstract void pausePlay();

    public abstract void userStopPlay();

    public abstract void stopPlay();

    public abstract void userResumePlay();

    public abstract void resumePlay();

    public abstract void forward();

    public abstract void backward();

    public abstract ACTION_TYPE getActionType();

    public enum ACTION_TYPE{
        MEDIA,VOICE
    }

}
