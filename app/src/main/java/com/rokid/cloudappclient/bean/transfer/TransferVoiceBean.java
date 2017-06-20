package com.rokid.cloudappclient.bean.transfer;

import com.rokid.cloudappclient.bean.base.BaseTransferBean;
import com.rokid.cloudappclient.bean.response.responseinfo.action.voice.VoiceBean;

/**
 * Author: xupan.shi
 * Version: V0.1 2017/3/14
 */
public class TransferVoiceBean extends BaseTransferBean {

    private int ttsId = -1;
    private VoiceBean voiceBean;

    public TransferVoiceBean(VoiceBean voiceBean) {
        this.voiceBean = voiceBean;
    }

    public int getTtsId() {
        return ttsId;
    }

    public void setTtsId(int ttsId) {
        this.ttsId = ttsId;
    }

    public VoiceBean getVoiceBean() {
        return voiceBean;
    }

    public void setVoiceBean(VoiceBean voiceBean) {
        this.voiceBean = voiceBean;
    }

    @Override
    public boolean isValid() {
        return null != voiceBean && voiceBean.isValid();
    }

    @Override
    public String getBehavior() {
        return getVoiceBean().getBehaviour();
    }

}
