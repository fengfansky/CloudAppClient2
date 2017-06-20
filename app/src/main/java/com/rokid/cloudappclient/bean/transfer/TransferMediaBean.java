package com.rokid.cloudappclient.bean.transfer;

import com.rokid.cloudappclient.bean.base.BaseTransferBean;
import com.rokid.cloudappclient.bean.response.responseinfo.action.media.MediaBean;

/**
 * Author: xupan.shi
 * Version: V0.1 2017/3/14
 */
public class TransferMediaBean extends BaseTransferBean {

    private String mediaType;
    private MediaBean mediaBean;

    public TransferMediaBean(MediaBean mediaBean) {
        this.mediaBean = mediaBean;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }


    public MediaBean getMediaBean() {
        return mediaBean;
    }

    public void setMediaBean(MediaBean mediaBean) {
        this.mediaBean = mediaBean;
    }

    @Override
    public boolean isValid() {
        return null != mediaBean && mediaBean.isValid();
    }

    @Override
    public String getBehavior() {
        return getMediaBean().getBehaviour();
    }

}
