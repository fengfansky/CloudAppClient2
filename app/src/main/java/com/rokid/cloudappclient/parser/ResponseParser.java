package com.rokid.cloudappclient.parser;

import com.google.gson.Gson;
import com.rokid.cloudappclient.bean.ActionNode;
import com.rokid.cloudappclient.bean.CommonResponse;
import com.rokid.cloudappclient.bean.response.CloudActionResponse;
import com.rokid.cloudappclient.proto.SendEvent;
import com.rokid.cloudappclient.reporter.BaseReporter;
import com.rokid.cloudappclient.state.BaseAppStateManager;
import com.rokid.cloudappclient.tts.TTSSpeakInterface;
import com.rokid.cloudappclient.util.AppTypeRecorder;
import com.rokid.cloudappclient.util.CommonResponseHelper;
import com.rokid.cloudappclient.util.Logger;
//import com.android.okhttp.Response;
import com.squareup.okhttp.Response;
import java.io.IOException;

/**
 * Created by fanfeng on 2017/6/1.
 */

public class ResponseParser {

    private BaseAppStateManager appStateManager = AppTypeRecorder.getInstance().getAppStateManager();

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

        Logger.d(" parse IntentResponse commonResponse : " + commonResponse);

        ActionNode actionNode = CommonResponseHelper.generateActionNode(commonResponse);

        //update appState
        appStateManager.onNewIntentActionNode(actionNode);

    }

    public void parseSendEventResponse(String event, Response response) {

        SendEvent.SendEventResponse eventResponse = null;

        try {
            eventResponse = SendEvent.SendEventResponse.parseFrom(response.body().source().readByteArray());
        } catch (IOException e) {
            e.printStackTrace();
            appStateManager.onEventErrorCallback(event, BaseReporter.ReporterResponseCallBack.ERROR_RESPONSE_NULL);
        }

        if (eventResponse == null) {
            Logger.d(" eventResponse is null");
            appStateManager.onEventErrorCallback(event, BaseReporter.ReporterResponseCallBack.ERROR_RESPONSE_NULL);
            return;
        }

        Logger.d(" eventResponse.response : " + eventResponse.getResponse());

        if (eventResponse.getResponse() == null) {
            Logger.d("eventResponse is null !");
            appStateManager.onEventErrorCallback(event, BaseReporter.ReporterResponseCallBack.ERROR_RESPONSE_NULL);
            return;
        }

        CloudActionResponse cloudResponse = new Gson().fromJson(eventResponse.getResponse(), CloudActionResponse.class);

        if (cloudResponse == null) {
            Logger.d("cloudResponse parsed null !");
            appStateManager.onEventErrorCallback(event, BaseReporter.ReporterResponseCallBack.ERROR_RESPONSE_NULL);
            return;
        }

        CommonResponse commonResponse = new CommonResponse();
        commonResponse.setAction(cloudResponse);
        ActionNode actionNode = CommonResponseHelper.generateActionNode(commonResponse);

        //update appState
        appStateManager.onNewEventActionNode(actionNode);

    }

}
