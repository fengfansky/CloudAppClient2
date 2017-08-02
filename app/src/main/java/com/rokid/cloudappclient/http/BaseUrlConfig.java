package com.rokid.cloudappclient.http;

import android.os.IBinder;
import android.text.TextUtils;

import com.rokid.cloudappclient.util.Logger;
import com.rokid.cloudappclient.util.MD5Utils;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by fanfeng on 2017/5/11.
 */
public class BaseUrlConfig {

    private static final String BASE_HTTP = "https://";

    private static final String DEFAULT_HOST = "apigwrest.open.rokid.com";

    private static final String SEND_EVENT_PATH = "/v1/skill/dispatch/sendEvent";

    private static String mHost;

    private static Map<String,String> deviceMap;

    private static final String KEY_HOST = "event_req_host";

    private static final String PARAM_KEY_KEY = "key";
    private static final String PARAM_KEY_DEVICE_TYPE_ID = "device_type_id";
    private static final String PARAM_KEY_DEVICE_ID = "device_id";
    private static final String PARAM_KEY_SERVICE = "service";
    private static final String PARAM_VALUE_SERVICE = "rest";
    private static final String PARAM_KEY_VERSION = "version";
    private static final String PARAM_KEY_TIME = "time";
    private static final String PARAM_KEY_SIGN = "sign";
    private static final String PARAM_KEY_SECRET = "secret";
    private static Map<String, String> params;


    private static void putUnEmptyParam(String key, String value) {
        if (TextUtils.isEmpty(key) || TextUtils.isEmpty(value)) {
            Logger.d("param invalidate ! key " + key + " value : " + value);
            return;
        }
        params.put(key, value);
    }

    public static void initDeviceInfo() {

        IBinder runtimeBinder ;
        try {
            Class<?> clazz = Class.forName("android.os.ServiceManager");
            Method method = clazz.getMethod("getService",String.class);
            runtimeBinder = (IBinder) method.invoke(null,"runtime_java");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        if(runtimeBinder == null){
            Logger.d(" runtime binder is null ");
            return;
        }
        rokid.os.IRuntimeService runtime = rokid.os.IRuntimeService.Stub.asInterface(runtimeBinder);
        try{
            deviceMap = runtime.getPlatformAccountInfo();
        }catch(Exception e){
            e.printStackTrace();
        }
        if (deviceMap == null || deviceMap.isEmpty()) {
            Logger.d(" deviceMap is null ");
            return;
        }

        Logger.d(" deviceMap is " + deviceMap.toString());

        mHost = deviceMap.get(KEY_HOST);

    }

    public static String getUrl() {

        if (mHost == null || mHost.isEmpty()){
            mHost = DEFAULT_HOST;
        }

        return BASE_HTTP + mHost + SEND_EVENT_PATH;
    }

    public static String getAuthorization() {

        params = new LinkedHashMap<>();

        if (deviceMap == null || deviceMap.isEmpty()) {
            Logger.e(" deviceMap is null ");
            return null;
        }

        putUnEmptyParam(PARAM_KEY_KEY, deviceMap.get(PARAM_KEY_KEY));
        putUnEmptyParam(PARAM_KEY_DEVICE_TYPE_ID, deviceMap.get(PARAM_KEY_DEVICE_TYPE_ID));
        putUnEmptyParam(PARAM_KEY_DEVICE_ID, deviceMap.get(PARAM_KEY_DEVICE_ID));

        putUnEmptyParam(PARAM_KEY_SERVICE, PARAM_VALUE_SERVICE);
        putUnEmptyParam(PARAM_KEY_VERSION, deviceMap.get("api_version"));
        putUnEmptyParam(PARAM_KEY_TIME, String.valueOf(System.currentTimeMillis()));
        putUnEmptyParam(PARAM_KEY_SIGN, MD5Utils.generateMD5(params, deviceMap.get(PARAM_KEY_SECRET)));
        if (params.isEmpty()){
            Logger.d("param is null !");
            return null;
        }
        Logger.d(" params : " + params.toString());

        String authorization = params.toString()
                .replace("{", "").replace("}", "").replace(",", ";").replace(" ", "");

        Logger.d(" authorization: " + authorization);
        return authorization;
    }


}
