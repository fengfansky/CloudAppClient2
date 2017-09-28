package com.rokid.cloudappclient.aidl;


import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;

import com.rokid.cloudappclient.AppTypeRecorder;
import com.rokid.logger.Logger;

/**
 * Created by fanfeng on 2017/8/29.
 */

public class CloudSceneService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    protected final Binder binder = new ICloudSceneService.Stub() {

        @Override
        public String getCloudStatus() throws RemoteException {
            String cloudStatus = AppTypeRecorder.getInstance().getCloudAppStatus();
            Logger.d("getCloudStatus scene ： " + cloudStatus);
            return cloudStatus;

        }
    };
}

