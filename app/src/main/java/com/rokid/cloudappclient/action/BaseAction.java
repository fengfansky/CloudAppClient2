package com.rokid.cloudappclient.action;

import com.rokid.cloudappclient.bean.base.BaseBean;

/**
 * Created by fanfeng on 2017/4/20.
 */

public abstract class BaseAction<T extends BaseBean> {

    public abstract void startAction(T actionBean);

    public abstract void pauseAction();

    public abstract void stopAction();

}
