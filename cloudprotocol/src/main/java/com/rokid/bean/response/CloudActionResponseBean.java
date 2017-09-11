package com.rokid.bean.response;

import android.text.TextUtils;

import com.rokid.bean.request.session.SessionBean;
import com.rokid.bean.response.responseinfo.ResponseBean;
import com.rokid.bean.response.responseinfo.action.ActionBean;
import com.rokid.bean.response.responseinfo.action.confirm.ConfirmBean;
import com.rokid.bean.response.responseinfo.action.media.MediaBean;
import com.rokid.bean.response.responseinfo.action.pickup.PickupBean;
import com.rokid.bean.response.responseinfo.action.voice.VoiceBean;
import com.rokid.logger.Logger;

/**
 * The response should be replied by CloudApps for client side execution.
 * <p>
 * Modified: fan.feng
 * Version: 2017/08/24
 */
public class CloudActionResponseBean {

    private static final String PROTOCOL_VERSION = "2.0.0";

    private String appId;
    private String version;
    private SessionBean session;
    private ResponseBean response;

    private String errorLog;

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

    public String getErrorLog() {
        return errorLog;
    }

    public void setErrorLog(String errorLog) {
        this.errorLog = errorLog;
        Logger.d(errorLog);
    }

    private static final String PROTOCOL_ERROR = "The field of version is null or not the same with " + PROTOCOL_VERSION + " !";
    private static final String RESPONSE_ERROR = "The field of response is null !";
    private static final String APPID_ERROR = "The field of appId is null !";
    private static final String ACTION_ERROR ="The field of action is null !";
    private static final String FORM_ERROR_NULL ="The field of form is null !";
    private static final String FORM_ERROR_ILLEGAL="The field of form you set is illegal !";
    private static final String NO_EXECUTABLE_ACTION = "No executable action , you must set up an executable action !";

    public boolean isValid(){

        // check version
        String version = getVersion();
        if (TextUtils.isEmpty(version) || !version.equals(PROTOCOL_VERSION)) {
            setErrorLog(PROTOCOL_ERROR);
            return false;
        }

        // check response
        if (getResponse() == null) {
            setErrorLog(RESPONSE_ERROR);
            return false;
        }

        if (TextUtils.isEmpty(getAppId())){
            setErrorLog(APPID_ERROR);
            return false;
        }

        if (getResponse().getAction() == null){
            setErrorLog(ACTION_ERROR);
            return false;
        }

        // check response form
        String form = getResponse().getAction().getForm();

        if (TextUtils.isEmpty(form)) {
            setErrorLog(FORM_ERROR_NULL);
            return false;
        }

        String formLow = form.toLowerCase();

        if (!formLow.equals(ActionBean.FORM_SCENE) && !formLow.equals(ActionBean.FORM_CUT)) {
            setErrorLog(FORM_ERROR_ILLEGAL);
            return false;
        }

        // check response action
        ActionBean responseAction = getResponse().getAction();
        if (responseAction == null) {
            setErrorLog(NO_EXECUTABLE_ACTION);
            return false;
        }

        // check response action type
        String responseActionType = responseAction.getType();

        if (responseActionType.equals(ActionBean.TYPE_EXIT)){
            Logger.d("actionType is EXIT ");
            return true;
        }

        if (!isDataValid(getResponse())){
            setErrorLog(NO_EXECUTABLE_ACTION);
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
        PickupBean pickupBean = responseAction.getPickup();

        if (mediaBean != null){
            if (mediaBean.isValid()){
                Logger.d("media valid ");
                return true;
            }else {
                setErrorLog(mediaBean.getErrorLog());
                return false;
            }
        }

        if (voiceBean != null){
            if (voiceBean.isValid()){
                Logger.d("voice valid ");
                return true;
            }else {
                setErrorLog(voiceBean.getErrorLog());
                return false;
            }
        }

        if (confirmBean != null){
            Logger.d("confirm valid ");
            return true;
        }

        if (pickupBean != null){
            Logger.d("pickup valid ");
            return true;
        }

        return false;
    }

}
