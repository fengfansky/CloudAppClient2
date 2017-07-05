package com.rokid.cloudappclient.parser;

import android.util.Log;

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

    //private BaseAppStateManager appStateManager = AppTypeRecorder.getInstance().getAppStateManager();

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
        Log.d("jiabin","parseIntentResponse----" + "commonResponse:" + commonResponse);

        Logger.d(" parse IntentResponse commonResponse : " + commonResponse);

        ActionNode actionNode = CommonResponseHelper.generateActionNode(commonResponse);

        //update appState
        AppTypeRecorder.getInstance().getAppStateManager().onNewIntentActionNode(actionNode);

    }

    public void parseSendEventResponse(String event, Response response) {

        Log.d("jiabin","parseSendEventResponse----" + "event:" + event + " | response:" + response);

        SendEvent.SendEventResponse eventResponse = null;

        try {
            eventResponse = SendEvent.SendEventResponse.parseFrom(response.body().source().readByteArray());
        } catch (IOException e) {
            e.printStackTrace();
            AppTypeRecorder.getInstance().getAppStateManager().onEventErrorCallback(event, BaseReporter.ReporterResponseCallBack.ERROR_RESPONSE_NULL);
        }

        if (eventResponse == null) {
            Logger.d(" eventResponse is null");
            AppTypeRecorder.getInstance().getAppStateManager().onEventErrorCallback(event, BaseReporter.ReporterResponseCallBack.ERROR_RESPONSE_NULL);
            return;
        }

        Logger.d(" eventResponse.response : " + eventResponse.getResponse());

        if (eventResponse.getResponse() == null) {
            Logger.d("eventResponse is null !");
            AppTypeRecorder.getInstance().getAppStateManager().onEventErrorCallback(event, BaseReporter.ReporterResponseCallBack.ERROR_RESPONSE_NULL);
            return;
        }

        CloudActionResponse cloudResponse = new Gson().fromJson(eventResponse.getResponse(), CloudActionResponse.class);

        if (cloudResponse == null) {
            Logger.d("cloudResponse parsed null !");
            AppTypeRecorder.getInstance().getAppStateManager().onEventErrorCallback(event, BaseReporter.ReporterResponseCallBack.ERROR_RESPONSE_NULL);
            return;
        }

        CommonResponse commonResponse = new CommonResponse();
        commonResponse.setAction(cloudResponse);
        ActionNode actionNode = CommonResponseHelper.generateActionNode(commonResponse);

        //update appState
        AppTypeRecorder.getInstance().getAppStateManager().onNewEventActionNode(actionNode);

    }

}
