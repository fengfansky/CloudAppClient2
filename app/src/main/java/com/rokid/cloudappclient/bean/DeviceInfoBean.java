package com.rokid.cloudappclient.bean;

import android.text.TextUtils;

public class DeviceInfoBean {

    private String key;
    private String deviceTypeId;
    private String deviceId;
    private String secret;
    private String apiVersion;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDeviceTypeId() {
        return deviceTypeId;
    }

    public void setDeviceTypeId(String device_type_id) {
        this.deviceTypeId = device_type_id;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String device_id) {
        this.deviceId = device_id;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public boolean isDeviceInfoValidate() {
        if (TextUtils.isEmpty(key) || TextUtils.isEmpty(deviceTypeId) || TextUtils.isEmpty(deviceId) || TextUtils.isEmpty(secret) || TextUtils.isEmpty(apiVersion)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "DeviceInfoBean{" +
                "key='" + key + '\'' +
                ", device_type_id='" + deviceTypeId + '\'' +
                ", device_id='" + deviceId + '\'' +
                ", secret='" + secret + '\'' +
                ", apiVersion='" + apiVersion + '\'' +
                '}';
    }
}
