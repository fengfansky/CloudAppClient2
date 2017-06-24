package com.rokid.cloudappclient.action;

import com.rokid.cloudappclient.bean.response.responseinfo.action.BaseActionBean;

/**
 * Created by fanfeng on 2017/4/20.
 */

public abstract class BaseAction<T extends BaseActionBean> {

    public abstract void startAction(T actionBean);

    public abstract void pauseAction();

    public abstract void stopAction();

}
