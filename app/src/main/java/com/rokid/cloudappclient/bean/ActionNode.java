package com.rokid.cloudappclient.bean;

import android.text.TextUtils;

import com.rokid.cloudappclient.bean.response.responseinfo.action.ActionBean;
import com.rokid.cloudappclient.bean.response.responseinfo.action.media.MediaBean;
import com.rokid.cloudappclient.bean.response.responseinfo.action.voice.VoiceBean;
import com.rokid.cloudappclient.util.Logger;

/**
 * Created by showingcp on 3/16/17.
 */

public class ActionNode {
    private String asr;
    private NLPBean nlp;
    private String respId;
    private String resType;
    private String appId;
    private String form;
    private String actionType;
    private boolean shouldEndSession;
    private VoiceBean voice;
    private MediaBean media;

    public String getAsr() {
        return asr;
    }

    public void setAsr(String asr) {
        this.asr = asr;
    }

    public NLPBean getNlp() {
        return nlp;
    }

    public void setNlp(NLPBean nlp) {
        this.nlp = nlp;
    }

    public String getRespId() {
        return respId;
    }

    public void setRespId(String respId) {
        this.respId = respId;
    }

    public String getResType() {
        return resType;
    }

    public void setResType(String respType) {
        this.resType = respType;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getForm() {
        return form;
    }

    public void setForm(String form) {
        this.form = form;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public boolean isShouldEndSession() {
        return shouldEndSession;
    }

    public void setShouldEndSession(boolean shouldEndSession) {
        this.shouldEndSession = shouldEndSession;
    }

    public VoiceBean getVoice() {
        return voice;
    }

    public void setVoice(VoiceBean voice) {
        this.voice = voice;
    }

    public MediaBean getMedia() {
        return media;
    }

    public void setMedia(MediaBean media) {
        this.media = media;
    }

    public boolean isValid(){
        if (TextUtils.isEmpty(appId)){
            Logger.d("appId is null !");
            return false;
        }
        if (TextUtils.isEmpty(form) || !ActionBean.FORM_SCENE.equals(form)){
            Logger.d("form invalid or form not scene!  form: " + form);
            return false;
        }
        if (voice == null && media == null){
            Logger.d("voice and media null ! invalid");
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "ActionNode{" +
                "asr='" + asr + '\'' +
                ", nlp=" + nlp +
                ", respId='" + respId + '\'' +
                ", resType='" + resType + '\'' +
                ", appId='" + appId + '\'' +
                ", form='" + form + '\'' +
                ", actionType='" + actionType + '\'' +
                ", shouldEndSession=" + shouldEndSession +
                ", voice=" + voice +
                ", media=" + media +
                '}';
    }
}
