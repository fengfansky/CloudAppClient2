package com.rokid.cloudappclient.action;

import com.rokid.cloudappclient.bean.base.BaseTransferBean;
import com.rokid.cloudappclient.util.Logger;

/**
 * Created by fanfeng on 2017/4/20.
 */

public abstract class BaseAction<T extends BaseTransferBean> {

    public T mTransfer;

    public abstract void startAction(T transfer);

    public abstract void pauseAction();

    public synchronized void stopAction(){
        if (mTransfer == null) {
            Logger.d("voiceTransfer == null");
            return;
        }
    }

    public abstract void resumeAction();

}
