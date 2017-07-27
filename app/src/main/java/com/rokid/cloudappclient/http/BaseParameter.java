package com.rokid.cloudappclient.http;

import android.os.IBinder;
import android.text.TextUtils;

import com.rokid.cloudappclient.util.Logger;
import com.rokid.cloudappclient.util.MD5Utils;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by fanfeng on 2017/5/15.
 */

public class BaseParameter {

    private static final String PARAM_KEY_KEY = "key";
    private static final String PARAM_KEY_DEVICE_TYPE_ID = "device_type_id";
    private static final String PARAM_KEY_DEVICE_ID = "device_id";
    private static final String PARAM_KEY_SERVICE = "service";
    private static final String PARAM_VALUE_SERVICE = "rest";
    private static final String PARAM_KEY_VERSION = "version";
    private static final String PARAM_KEY_TIME = "time";
    private static final String PARAM_KEY_SIGN = "sign";
    private static final String PARAM_KEY_SECRET = "secret";


    Map<String, String> params;

    public void putUnEmptyParam(String key, String value) {
        if (TextUtils.isEmpty(key) || TextUtils.isEmpty(value)) {
            Logger.d("param invalidate ! key " + key + " value : " + value);
            return;
        }
        params.put(key, value);
    }

    private IBinder invokeServiceManager(){
        try {
            Class<?> clazz = Class.forName("android.os.ServiceManager");
            Method method = clazz.getMethod("getService",String.class);
            return (IBinder) method.invoke(null,"runtime_java");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }


    }

    private Map<String, String> generateParams() {
        IBinder runtime_binder = invokeServiceManager();
        if(runtime_binder == null){
            Logger.d(" runtime binder is null ");
            return null;
        }
        rokid.os.IRuntimeService runtime = rokid.os.IRuntimeService.Stub.asInterface(runtime_binder);
        Map<String, String> deviceMap = null;
        try{
            deviceMap = runtime.getPlatformAccountInfo();
        }catch(Exception e){
            e.printStackTrace();
        }
        if (deviceMap == null || deviceMap.isEmpty()) {
            Logger.d(" deviceMap is null ");
            return null;
        }

        Logger.d(" deviceMap is " + deviceMap.toString());

        params = new LinkedHashMap<>();

        putUnEmptyParam(PARAM_KEY_KEY, deviceMap.get(PARAM_KEY_KEY));
        putUnEmptyParam(PARAM_KEY_DEVICE_TYPE_ID, deviceMap.get(PARAM_KEY_DEVICE_TYPE_ID));
        putUnEmptyParam(PARAM_KEY_DEVICE_ID, deviceMap.get(PARAM_KEY_DEVICE_ID));

        putUnEmptyParam(PARAM_KEY_SERVICE, PARAM_VALUE_SERVICE);
        putUnEmptyParam(PARAM_KEY_VERSION, deviceMap.get("api_version"));
        putUnEmptyParam(PARAM_KEY_TIME, String.valueOf(System.currentTimeMillis()));
        putUnEmptyParam(PARAM_KEY_SIGN, MD5Utils.generateMD5(params, deviceMap.get(PARAM_KEY_SECRET)));
        Logger.d(" params : " + params.toString());
        return params;
    }

    public String getAuthorization() {
        generateParams();
        if (params == null && params.isEmpty()) {
            Logger.d("param invalidate !!!");
            return null;
        }

        String authorization = params.toString()
                .replace("{", "").replace("}", "").replace(",", ";").replace(" ", "");
        return authorization;
    }

}
