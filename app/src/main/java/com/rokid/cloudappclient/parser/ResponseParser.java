package com.rokid.cloudappclient.parser;

import android.content.Intent;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.rokid.cloudappclient.bean.ActionNode;
import com.rokid.cloudappclient.bean.NLPBean;
import com.rokid.cloudappclient.bean.response.CloudActionResponseBean;
import com.rokid.cloudappclient.player.ErrorPromoter;
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

    private static final String KEY_NLP = "nlp";
    private static final String KEY_ACTION = "action";
    private static final String KEY_COMMON_RESPONSE = "extra";

    public void parseIntent(Intent intent) throws IOException {

        if (intent == null) {
            Logger.d("intent null !");
            ErrorPromoter.getInstance().speakErrorPromote(ErrorPromoter.ERROR_TYPE.DATA_INVALID,null);
            return;
        }

        String nlp = intent.getStringExtra(KEY_NLP);
        if (TextUtils.isEmpty(nlp)) {
            Logger.d("NLP is empty!!!");
            ErrorPromoter.getInstance().speakErrorPromote(ErrorPromoter.ERROR_TYPE.DATA_INVALID, null);
            return;
        }

        Logger.d("parseIntent Nlp ---> ", nlp);
        NLPBean nlpBean = null;
        try{
            nlpBean = new Gson().fromJson(nlp, NLPBean.class);
        }catch (JsonParseException jsonException){
            Logger.e(" json exception ! " + jsonException.getMessage());
            ErrorPromoter.getInstance().speakErrorPromote(ErrorPromoter.ERROR_TYPE.DATA_INVALID, null);
            jsonException.printStackTrace();
        }

        if (null == nlpBean) {
            Logger.d("NLPData is empty!!!");
            ErrorPromoter.getInstance().speakErrorPromote(ErrorPromoter.ERROR_TYPE.DATA_INVALID, null);
            return;
        }

        String actionStr = nlpBean.getAction();

        CloudActionResponseBean actionBean = null;

        try{
            actionBean = new Gson().fromJson(actionStr, CloudActionResponseBean.class);
        }catch (JsonParseException jsonException){
            Logger.e(" json exception ! " + jsonException.getMessage());
            ErrorPromoter.getInstance().speakErrorPromote(ErrorPromoter.ERROR_TYPE.DATA_INVALID, null);
            jsonException.printStackTrace();
        }

        Logger.d(" parse IntentResponse commonResponse : " + actionBean);

        ActionNode actionNode = CommonResponseHelper.generateActionNode(actionBean);

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

        CloudActionResponseBean cloudResponse = null;
        try{
            cloudResponse = new Gson().fromJson(eventResponse.getResponse(), CloudActionResponseBean.class);
        }catch (JsonParseException jsonException){
            Logger.e(" json exception ! " + jsonException.getMessage());
            jsonException.printStackTrace();
        }

        if (cloudResponse == null) {
            Logger.d("cloudResponse parsed null !");
            AppTypeRecorder.getInstance().getAppStateManager().onEventErrorCallback(event, BaseReporter.ReporterResponseCallBack.ERROR_CODE.ERROR_RESPONSE_NULL);
            return;
        }

        ActionNode actionNode = CommonResponseHelper.generateActionNode(cloudResponse);

        //update appState
        AppTypeRecorder.getInstance().getAppStateManager().onNewEventActionNode(actionNode);

    }

}
