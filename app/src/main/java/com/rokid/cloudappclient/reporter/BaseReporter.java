package com.rokid.cloudappclient.reporter;

import com.rokid.cloudappclient.http.BaseUrlConfig;
import com.rokid.cloudappclient.http.HttpClientWrapper;
import com.rokid.cloudappclient.proto.SendEvent;
import com.rokid.cloudappclient.proto.SendEventCreator;
import com.rokid.cloudappclient.state.AppTypeRecorder;
import com.rokid.cloudappclient.util.Logger;
//import com.android.okhttp.Response;
import com.squareup.okhttp.Response;

import java.io.IOException;


/**
 * Created by fanfeng on 2017/5/9.
 */

public abstract class BaseReporter implements Runnable {

    String appId;
    String event;
    String extra;

    public BaseReporter(String appId, String event){
        this.appId = appId;
        this.event = event;
        this.extra = "{}";
    }

    public BaseReporter(String appId, String event, String extra) {
        this.appId = appId;
        this.event = event;
        this.extra = extra;
    }

    @Override
    public void run() {
        report();
    }

    public void report() {

        if (AppTypeRecorder.getInstance().getAppStateManager() == null) {
            Logger.d("appStateManager is null ");
            return;
        }


        SendEvent.SendEventRequest eventRequest =
                SendEventCreator.generateSendEventRequest(appId, event, extra);
        Logger.d(" eventRequest : " + eventRequest.toString());
        Logger.d(" eventRequest url: " + BaseUrlConfig.getUrl());
        Response response = null;
        try {
            response = HttpClientWrapper.getInstance().sendRequest(BaseUrlConfig.getUrl(), eventRequest);
        } catch (IOException e) {
            e.printStackTrace();
            AppTypeRecorder.getInstance().getAppStateManager().onEventErrorCallback(event, ReporterResponseCallBack.ERROR_CODE.ERROR_IOEXCEPTION);
        }finally {
            try {
                if (response != null && response.body() != null){
                    AppTypeRecorder.getInstance().getAppStateManager().onEventResponseCallback(event, response);
                    response.body().close();
                }else {
                    AppTypeRecorder.getInstance().getAppStateManager().onEventErrorCallback(event, ReporterResponseCallBack.ERROR_CODE.ERROR_RESPONSE_NULL);
                }
            } catch (IOException e) {
                e.printStackTrace();
                AppTypeRecorder.getInstance().getAppStateManager().onEventErrorCallback(event, ReporterResponseCallBack.ERROR_CODE.ERROR_IOEXCEPTION);
            }
        }


    }

    public interface ReporterResponseCallBack {

        enum ERROR_CODE{
            ERROR_CONNNECTION_TIMEOUT,
            ERROR_RESPONSE_NULL,
            ERROR_IOEXCEPTION ,
            ERROR_PARSE_EXCEPTION

        }

        void onEventErrorCallback(String event, ERROR_CODE errorCode);

        void onEventResponseCallback(String event, Response response);
    }
}
