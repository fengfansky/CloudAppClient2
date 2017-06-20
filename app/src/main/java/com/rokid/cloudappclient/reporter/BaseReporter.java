package com.rokid.cloudappclient.reporter;

import com.rokid.cloudappclient.http.BaseParameter;
import com.rokid.cloudappclient.http.BaseUrlConfig;
import com.rokid.cloudappclient.http.HttpClientWrapper;
import com.rokid.cloudappclient.proto.SendEvent;
import com.rokid.cloudappclient.proto.SendEventCreator;
import com.rokid.cloudappclient.util.AppTypeRecorder;
import com.rokid.cloudappclient.util.DeviceInfoUtil;
import com.rokid.cloudappclient.util.Logger;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.net.SocketTimeoutException;


/**
 * Created by fanfeng on 2017/5/9.
 */

public abstract class BaseReporter implements Runnable {

    String event;
    String extra;

    public BaseReporter(String event){
        this.event = event;
        this.extra = "{}";
    }

    public BaseReporter(String event, String extra) {
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
                SendEventCreator.generateSendEventRequest(DeviceInfoUtil.getDeviceAppId(), event, extra);
        BaseParameter baseParameter = new BaseParameter();
        Response response = HttpClientWrapper.getInstance().sendRequest(BaseUrlConfig.getUrl(), baseParameter, eventRequest);

        if (response == null || response.body() == null) {
            AppTypeRecorder.getInstance().getAppStateManager().onEvnetErrorCallback(event, ReporterResponseCallBack.ERROR_RESPONSE_NULL);
            return;
        }

        AppTypeRecorder.getInstance().getAppStateManager().responseCallback(event, response);
        try {
            response.body().close();
        } catch (SocketTimeoutException socketTimeoutException) {
            AppTypeRecorder.getInstance().getAppStateManager().onEvnetErrorCallback(event, ReporterResponseCallBack.ERROR_CONNNECTION_TIMEOUT);
        } catch (IOException e) {
            AppTypeRecorder.getInstance().getAppStateManager().onEvnetErrorCallback(event, ReporterResponseCallBack.ERROR_IOEXCEPTION);
            e.printStackTrace();
        }
    }

    public interface ReporterResponseCallBack {

        int ERROR_CONNNECTION_TIMEOUT = 0;
        int ERROR_RESPONSE_NULL = 1;
        int ERROR_IOEXCEPTION = 2;

        void onEvnetErrorCallback(String event, int errorCode);

        void responseCallback(String event, Response response);
    }
}
