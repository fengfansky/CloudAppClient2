package com.rokid.cloudappclient.state;

/**
 * Created by fanfeng on 2017/6/14.
 */

public interface VoiceStateCallback {

    void onVoiceStart();

    void onVoiceStop();

    void onVoiceCancled();

    void onVoiceError();
}
