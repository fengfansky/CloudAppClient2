package com.rokid.reporter;

import com.rokid.http.BaseUrlConfig;
import com.rokid.http.HttpClientWrapper;
import com.rokid.logger.Logger;
import com.rokid.proto.SendEvent;
import com.rokid.proto.SendEventCreator;
import com.squareup.okhttp.Response;

import java.io.IOException;

//import com.android.okhttp.Response;


/**
 * Created by fanfeng on 2017/5/9.
 */

public abstract class BaseReporter implements Runnable {

    String appId;
    String event;
    String extra;
    ReporterResponseCallback mResponseCallback;

    public BaseReporter(String appId, String event,ReporterResponseCallback reporterResponseCallback){
        this.appId = appId;
        this.event = event;
        this.extra = "{}";
        this.mResponseCallback = reporterResponseCallback;
    }

    public BaseReporter(String appId, String event, String extra, ReporterResponseCallback reporterResponseCallback) {
        this.appId = appId;
        this.event = event;
        this.extra = extra;
        this.mResponseCallback = reporterResponseCallback;
    }

    @Override
    public void run() {
        report();
    }

    public void report() {

        SendEvent.SendEventRequest eventRequest =
                SendEventCreator.generateSendEventRequest(appId, event, extra);
        Logger.d(" eventRequest : " + eventRequest.toString());
        Logger.d(" eventRequest url: " + BaseUrlConfig.getUrl());
        Response response = null;
        try {
            response = HttpClientWrapper.getInstance().sendRequest(BaseUrlConfig.getUrl(), eventRequest);
        } catch (IOException e) {
            e.printStackTrace();
            mResponseCallback.onEventErrorCallback(event, ReporterResponseCallback.ERROR_CODE.ERROR_IOEXCEPTION);
        }finally {
            try {
                if (response != null && response.body() != null){
                    mResponseCallback.onEventResponseCallback(event, response);
                    response.body().close();
                }else {
                    mResponseCallback.onEventErrorCallback(event, ReporterResponseCallback.ERROR_CODE.ERROR_RESPONSE_NULL);
                }
            } catch (IOException e) {
                e.printStackTrace();
                mResponseCallback.onEventErrorCallback(event, ReporterResponseCallback.ERROR_CODE.ERROR_IOEXCEPTION);
            }
        }

    }

    public interface ReporterResponseCallback {

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
