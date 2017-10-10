package com.rokid.cloudappclient.event;

import android.os.IBinder;

import com.rokid.logger.Logger;
import com.rokid.reporter.BaseRuntimeCreator;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by fanfeng on 2017/9/17.
 */

public class SystemServiceHelper extends BaseRuntimeCreator {

    @Override
    public Map<String,String> getEnvParam(){
        IBinder runtimeBinder = getRuntimeBinder();

        if(runtimeBinder == null){
            Logger.d(" runtime binder is null ");
            return null;
        }
        rokid.os.IRuntimeService runtime = rokid.os.IRuntimeService.Stub.asInterface(runtimeBinder);

        Map<String, String> deviceMap = null;
        try{
            deviceMap = runtime.getPlatformAccountInfo();
            Logger.d(" deviceMap is " + deviceMap.toString());
        }catch(Exception e){
            e.printStackTrace();
        }
        if (deviceMap == null || deviceMap.isEmpty()) {
            Logger.d(" deviceMap is null ");
            return null;
        }

        return deviceMap;
    }

    private IBinder getRuntimeBinder() {
        IBinder runtimeBinder = null;
        try {
            Class<?> clazz = Class.forName("android.os.ServiceManager");
            Method method = clazz.getMethod("getService",String.class);
            runtimeBinder = (IBinder) method.invoke(null,"runtime_java");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return runtimeBinder;
    }

}
