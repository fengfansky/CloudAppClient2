package com.rokid.cloudappclient.light;

import android.os.RemoteException;

import com.rokid.light.BaseLightHelper;
import com.rokid.logger.Logger;

import rokid.os.IRKLight;
import rokid.services.util.RemoteServiceHelper;

/**
 * Created by fanfeng on 2017/9/21.
 */

public class LightHelper extends BaseLightHelper{

   private IRKLight rkLight;

    public LightHelper initLight(){
        rkLight = RemoteServiceHelper.getService(RemoteServiceHelper.RK_LIGHT);
        return this;
    }

    @Override
    public void openLoadingLight() {
        Logger.d(" openLight ");
        try {
            rkLight.setMindPause();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void closeLight() {
        Logger.d(" closeLight ");
        try {
            rkLight.setMindExec();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


}
