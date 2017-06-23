package com.rokid.cloudappclient.bean.response;

import android.text.TextUtils;

import com.rokid.cloudappclient.bean.request.session.SessionBean;
import com.rokid.cloudappclient.bean.response.responseinfo.ResponseBean;
import com.rokid.cloudappclient.bean.response.responseinfo.action.ActionBean;
import com.rokid.cloudappclient.bean.response.responseinfo.action.media.MediaBean;
import com.rokid.cloudappclient.bean.response.responseinfo.action.voice.VoiceBean;
import com.rokid.cloudappclient.util.Logger;

/**
 * The response should be replied by CloudApps for client side execution.
 * <p>
 * Author: xupan.shi
 * Version: V0.1 2017/3/7
 */
public class CloudActionResponse {

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

        // check response form
        String form = getResponse().getAction().getForm().toLowerCase();

        if (TextUtils.isEmpty(form)) {
            Logger.i("checkCloudAppAction: form for response is invalid");
            return false;
        }

        if (!form.equals(ActionBean.FORM_SCENE) && !form.equals(ActionBean.FORM_CUT)) {
            Logger.i("checkCloudAppAction: form not a scene !" + form );
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

        if (!responseActionType.equals(ActionBean.TYPE_NORMAL)
                && !responseActionType.equals(ActionBean.TYPE_EXIT)) {
            Logger.i("checkCloudAppAction: ignore unknown response action type: " + responseActionType);
            return false;
        }

        return true;
    }


    /**
     * Private method to check voice, media and display
     *
     */
    private boolean checkActionElements(ResponseBean responseBean) {
        ActionBean responseAction = responseBean.getAction();

        MediaBean mediaBean = responseAction.getMedia();
        VoiceBean voiceBean = responseAction.getVoice();

        if (mediaBean == null && voiceBean == null){
            Logger.d("media and voice is null! ");
            return false;
        }else if (mediaBean == null && voiceBean != null){
            if (voiceBean.isValid()){
                return true;
            }else {
                Logger.d("media null , voice invalid! ");
                return false;
            }
        }else if (voiceBean == null && mediaBean != null){
            if (mediaBean.isValid()){
                return true;
            }else {
                Logger.d(" voice null , media invalid!");
                return false;
            }
        }else if (!mediaBean.isValid() && !voiceBean.isValid()){
            Logger.d(" voice and media invalid !");
            return false;
        }

        return true;
    }

}
