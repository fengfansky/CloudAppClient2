package com.rokid.cloudappclient.parser;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.rokid.cloudappclient.action.MediaAction;
import com.rokid.cloudappclient.action.VoiceAction;
import com.rokid.cloudappclient.bean.ActionNode;
import com.rokid.cloudappclient.bean.CommonResponse;
import com.rokid.cloudappclient.bean.response.CloudActionResponse;
import com.rokid.cloudappclient.bean.response.responseinfo.action.ActionBean;
import com.rokid.cloudappclient.bean.transfer.TransferMediaBean;
import com.rokid.cloudappclient.bean.transfer.TransferVoiceBean;
import com.rokid.cloudappclient.proto.SendEvent;
import com.rokid.cloudappclient.reporter.BaseReporter;
import com.rokid.cloudappclient.tts.TTSSpeakInterface;
import com.rokid.cloudappclient.util.AppTypeRecorder;
import com.rokid.cloudappclient.util.CommonResponseHelper;
import com.rokid.cloudappclient.util.Logger;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * Created by fanfeng on 2017/6/1.
 */

public class ResponseParser {

    private static ResponseParser parser;

    public static ResponseParser getInstance() {
        if (parser == null) {
            synchronized (ResponseParser.class) {
                if (parser == null)
                    parser = new ResponseParser();
            }
        }
        return parser;
    }

    TTSSpeakInterface mTtsSpeakInterface;

    public void setTTSSpeakInterface(TTSSpeakInterface ttsSpeakInterface) {
        mTtsSpeakInterface = ttsSpeakInterface;
    }

    public void parseIntentResponse(CommonResponse commonResponse) {

        ActionNode actionNode = CommonResponseHelper.generateActionNode(commonResponse);

        // update current application info for further use. App info consists: DOMAIN and SHOT
        if (actionNode == null) {
            Logger.d("actionNode is null!");
            return;
        }

        String lastAppId = AppTypeRecorder.getInstance().getAppStateManager().getAppId();

        if (!actionNode.getAppId().equals(lastAppId)) {
            Logger.d("new cloudApp coming , onNewActionNode");
            AppTypeRecorder.getInstance().getAppStateManager().onNewActionNode(actionNode);
        }
        processActionNode(actionNode);
    }

    public void parseSendEventResponse(String event, Response response) {

        SendEvent.SendEventResponse eventResponse = null;

        try {
            eventResponse = SendEvent.SendEventResponse.parseFrom(response.body().source().readByteArray());
        } catch (IOException e) {
            e.printStackTrace();
            AppTypeRecorder.getInstance().getAppStateManager().onEvnetErrorCallback(event, BaseReporter.ReporterResponseCallBack.ERROR_RESPONSE_NULL);
        }

        if (eventResponse == null) {
            Logger.d(" eventResponse is null");
            AppTypeRecorder.getInstance().getAppStateManager().onEvnetErrorCallback(event, BaseReporter.ReporterResponseCallBack.ERROR_RESPONSE_NULL);
            return;
        }

        Logger.d(" eventResponse.response : " + eventResponse.getResponse());

        if (eventResponse.getResponse() == null) {
            Logger.d("eventResponse is null !");
            AppTypeRecorder.getInstance().getAppStateManager().onEvnetErrorCallback(event, BaseReporter.ReporterResponseCallBack.ERROR_RESPONSE_NULL);
            return;
        }

        CloudActionResponse cloudResponse = new Gson().fromJson(eventResponse.getResponse(), CloudActionResponse.class);

        if (cloudResponse == null) {
            Logger.d("cloudResponse parsed null !");
            AppTypeRecorder.getInstance().getAppStateManager().onEvnetErrorCallback(event, BaseReporter.ReporterResponseCallBack.ERROR_RESPONSE_NULL);
            return;
        }
        if (TextUtils.isEmpty(cloudResponse.getAppId())) {
            Logger.d(" cloudAppId is null !");
            return;
        }

        String currentAppId = AppTypeRecorder.getInstance().getAppStateManager().getAppId();
        //filter the cloudResponse appId that not the same with last app
        if (!cloudResponse.getAppId().equals(currentAppId)) {
            Logger.d("eventResponse cloudAppId is not the same with currentAppId ! cloudAppId : " + cloudResponse.getAppId() + " currentAppId : " + currentAppId);
            return;
        }

        CommonResponse commonResponse = new CommonResponse();
        commonResponse.setAction(cloudResponse);
        ActionNode actionNode = CommonResponseHelper.generateActionNode(commonResponse);
        processActionNode(actionNode);

    }

    /**
     * To process real action
     *
     * @param actionNode the validated action
     */
    private void processActionNode(ActionNode actionNode) {
        if (actionNode == null)
            return;

        if (ActionBean.TYPE_EXIT.equals(actionNode.getActionType())) {
            Logger.d("current response is a INTENT EXIT - Finish Activity");
            AppTypeRecorder.getInstance().getAppStateManager().finishActivity();
        }

        if (ActionBean.TYPE_NORMAL.equals(actionNode.getActionType())) {

            if (actionNode.getVoice() != null) {
                VoiceAction.getInstance().startAction(new TransferVoiceBean(actionNode.getVoice()));
            }
            if (actionNode.getMedia() != null) {
                MediaAction.getInstance().startAction(new TransferMediaBean(actionNode.getMedia()));
            }
        }

    }


}
