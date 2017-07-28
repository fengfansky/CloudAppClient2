package com.rokid.cloudappclient.parser;

import com.google.gson.Gson;
import com.rokid.cloudappclient.bean.ActionNode;
import com.rokid.cloudappclient.bean.CommonResponseBean;
import com.rokid.cloudappclient.bean.response.CloudActionResponseBean;
import com.rokid.cloudappclient.proto.SendEvent;
import com.rokid.cloudappclient.reporter.BaseReporter;
import com.rokid.cloudappclient.state.AppTypeRecorder;
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

    public void parseIntentResponse(CommonResponseBean commonResponse) {

        Logger.d(" parse IntentResponse commonResponse : " + commonResponse);

        ActionNode actionNode = CommonResponseHelper.generateActionNode(commonResponse);

        //update appState
        AppTypeRecorder.getInstance().getAppStateManager().onNewIntentActionNode(actionNode);

    }

    public void parseSendEventResponse(String event, Response response) {

        SendEvent.SendEventResponse eventResponse = null;

        try {
            eventResponse = SendEvent.SendEventResponse.parseFrom(response.body().source().readByteArray());
        } catch (IOException e) {
            e.printStackTrace();
            AppTypeRecorder.getInstance().getAppStateManager().onEventErrorCallback(event, BaseReporter.ReporterResponseCallBack.ERROR_CODE.ERROR_PARSE_EXCEPTION);
        }

        if (eventResponse == null) {
            Logger.d(" eventResponse is null");
            AppTypeRecorder.getInstance().getAppStateManager().onEventErrorCallback(event, BaseReporter.ReporterResponseCallBack.ERROR_CODE.ERROR_RESPONSE_NULL);
            return;
        }

        Logger.d(" eventResponse.response : " + eventResponse.getResponse());

        if (eventResponse.getResponse() == null) {
            Logger.d("eventResponse is null !");
            AppTypeRecorder.getInstance().getAppStateManager().onEventErrorCallback(event, BaseReporter.ReporterResponseCallBack.ERROR_CODE.ERROR_RESPONSE_NULL);
            return;
        }

        CloudActionResponseBean cloudResponse = new Gson().fromJson(eventResponse.getResponse(), CloudActionResponseBean.class);

        if (cloudResponse == null) {
            Logger.d("cloudResponse parsed null !");
            AppTypeRecorder.getInstance().getAppStateManager().onEventErrorCallback(event, BaseReporter.ReporterResponseCallBack.ERROR_CODE.ERROR_RESPONSE_NULL);
            return;
        }

        CommonResponseBean commonResponse = new CommonResponseBean();
        commonResponse.setAction(cloudResponse);
        ActionNode actionNode = CommonResponseHelper.generateActionNode(commonResponse);

        //update appState
        AppTypeRecorder.getInstance().getAppStateManager().onNewEventActionNode(actionNode);

    }

}
