package com.rokid.cloudappclient.state;

/**
 * Created by fanfeng on 2017/6/14.
 */

public interface MediaStateCallback {

    void onMediaStart();

    void onMediaPause(int position);

    void onMediaResume();

    void onMediaStop();

    void onMediaError();
}
