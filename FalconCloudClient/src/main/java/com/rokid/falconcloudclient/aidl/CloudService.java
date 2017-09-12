package com.rokid.falconcloudclient.aidl;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;

import com.rokid.falconcloudclient.FalconCloudTask;

/**
 * Created by fanfeng on 2017/8/29.
 */

public class CloudService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    protected final Binder binder = new ICloudService.Stub() {

        @Override
        public String getCloudStatus() throws RemoteException {

            return FalconCloudTask.getInstance().getCloudStateMonitor().getCloudStatus();

        }
    };
}

