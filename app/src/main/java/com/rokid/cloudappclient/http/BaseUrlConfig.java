package com.rokid.cloudappclient.http;


/**
 * Created by fanfeng on 2017/5/11.
 */

public class BaseUrlConfig {



    public static final String BASE_URL = "https://apigwrest.open.rokid.com";

    public static final String BASE_URL_TEST = "https://apigwrest-dev.open.rokid.com";

    public static boolean isDev = true;

    public static final String PATH = "/v1/skill/dispatch/sendEvent";

    public static String getUrl() {

        String baseUrl;
        if (isDev){
            baseUrl = BASE_URL_TEST.concat(PATH);
        }else {
            baseUrl = BASE_URL.concat(PATH);
        }
        return baseUrl;
    }

}
