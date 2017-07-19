package com.rokid.cloudappclient.bean.response;

import android.text.TextUtils;

import com.rokid.cloudappclient.bean.request.session.SessionBean;
import com.rokid.cloudappclient.bean.response.responseinfo.ResponseBean;
import com.rokid.cloudappclient.bean.response.responseinfo.action.ActionBean;
import com.rokid.cloudappclient.bean.response.responseinfo.action.confirm.ConfirmBean;
import com.rokid.cloudappclient.bean.response.responseinfo.action.media.MediaBean;
import com.rokid.cloudappclient.bean.response.responseinfo.action.voice.VoiceBean;
import com.rokid.cloudappclient.util.Logger;

/**
 * The response should be replied by CloudApps for client side execution.
 * <p>
 * Author: xupan.shi
 * Version: V0.1 2017/3/7
 */
public class CloudActionResponseBean {

    private static final String PROTOCOL_VERSION = "2.0.0";

    private String appId;
    private String version;
    private SessionBean session;
    private ResponseBean response;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public SessionBean getSession() {
        return session;
    }

    public void setSession(SessionBean session) {
        this.session = session;
    }

    public ResponseBean getResponse() {
        return response;
    }

    public void setResponse(ResponseBean response) {
        this.response = response;
    }

    public boolean isValid(){

        // check version
        String version = getVersion();
        if (TextUtils.isEmpty(version) || !version.equals(PROTOCOL_VERSION)) {
            Logger.i("checkCloudAppAction: given protocol version: " + version + " is invalid");
            return false;
        }

        // check response
        if (getResponse() == null) {
            Logger.i("checkAction: response of action is null");
            return false;
        }

        if (TextUtils.isEmpty(getAppId())){
            Logger.d("checkAppId : appId is null ");
            return false;
        }

        if (getResponse().getAction() == null){
            Logger.d("action is null !");
            return false;
        }

        // check response form
        String form = getResponse().getAction().getForm();

        if (TextUtils.isEmpty(form)) {
            Logger.i("checkCloudAppAction: form for response is invalid");
            return false;
        }

        String formLow = form.toLowerCase();

        if (!formLow.equals(ActionBean.FORM_SCENE) && !formLow.equals(ActionBean.FORM_CUT)) {
            Logger.i("checkCloudAppAction: form not a scene !" + formLow );
            return false;
        }

        // check response type
        String resType = getResponse().getResType();
        if (TextUtils.isEmpty(resType)) {
            Logger.i("checkCloudAppAction: resType is invalid");
            return false;
        }

        if (!resType.equals(ResponseBean.TYPE_INTENT)
                && !resType.equals(ResponseBean.TYPE_EVENT)) {
            Logger.i("checkCloudAppAction: ignore for unknown resType: " + resType);
            return false;
        }

        // check response action
        ActionBean responseAction = getResponse().getAction();
        if (responseAction == null) {
            Logger.i("checkCloudAppAction: response action is null");
            return false;
        }

        // check response action type
        String responseActionType = responseAction.getType();
        if (TextUtils.isEmpty(responseActionType)) {
            Logger.i("checkCloudAppAction: response action type is invalid");
            return false;
        }

        if (responseActionType.equals(ActionBean.TYPE_EXIT)){
            Logger.d("actionType is EXIT ");
            return true;
        }

        if (!isDataValid(getResponse())){
            Logger.d("media and voice both invalid!");
            return false;
        }

        return true;
    }


    /**
     * Private method to check voice, media and display
     *
     */
    private boolean isDataValid(ResponseBean responseBean) {
        ActionBean responseAction = responseBean.getAction();

        MediaBean mediaBean = responseAction.getMedia();
        VoiceBean voiceBean = responseAction.getVoice();
        ConfirmBean confirmBean = responseAction.getConfirm();

        if (mediaBean != null && mediaBean.isValid()){
            Logger.d("mediaBean valid ");
            return true;
        }

        if (voiceBean != null && voiceBean.isValid()){
            return true;
        }

        if (confirmBean != null){
            return true;
        }

        return false;
    }

}
