package com.rokid.cloudappclient.reporter;

import com.rokid.cloudappclient.http.BaseParameter;
import com.rokid.cloudappclient.http.BaseUrlConfig;
import com.rokid.cloudappclient.http.HttpClientWrapper;
import com.rokid.cloudappclient.proto.SendEvent;
import com.rokid.cloudappclient.proto.SendEventCreator;
import com.rokid.cloudappclient.util.AppTypeRecorder;
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


    public void setEvent(String event) {
        this.event = event;
    }

    //TODO set extra value
    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getEvent() {
        return event;
    }

    public String getExtra() {
        return extra;
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

        if (AppTypeRecorder.getInstance().getAppStateManager().isShouldEndSession()) {
            Logger.d("cloudscene isShouldEndSession true , dont't sendEventReport !");
            return;
        }

        SendEvent.SendEventRequest eventRequest =
                SendEventCreator.generateSendEventRequest(appId, event, extra);
        Logger.d(" eventRequest : " + eventRequest.toString());
        BaseParameter baseParameter = new BaseParameter();
        Response response = null;
        try {
            response = HttpClientWrapper.getInstance().sendRequest(BaseUrlConfig.getUrl(), baseParameter, eventRequest);
        } catch (IOException e) {
            AppTypeRecorder.getInstance().getAppStateManager().onEventErrorCallback(event, ReporterResponseCallBack.ERROR_IOEXCEPTION);
            Logger.e(" response callback exception !");
        }finally {
            try {
                if (response != null && response.body() != null){
                    AppTypeRecorder.getInstance().getAppStateManager().responseCallback(event, response);
                    response.body().close();
                }else {
                    AppTypeRecorder.getInstance().getAppStateManager().onEventErrorCallback(event, ReporterResponseCallBack.ERROR_RESPONSE_NULL);
                }
            } catch (IOException e) {
                AppTypeRecorder.getInstance().getAppStateManager().onEventErrorCallback(event, ReporterResponseCallBack.ERROR_IOEXCEPTION);
                Logger.e(" response close exception !");
            }
        }


    }

    public interface ReporterResponseCallBack {

        int ERROR_CONNNECTION_TIMEOUT = 0;
        int ERROR_RESPONSE_NULL = 1;
        int ERROR_IOEXCEPTION = 2;

        void onEventErrorCallback(String event, int errorCode);

        void responseCallback(String event, Response response);
    }
}
