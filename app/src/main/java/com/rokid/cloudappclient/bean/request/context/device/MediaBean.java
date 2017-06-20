package com.rokid.cloudappclient.bean.request.context.device;

/**
 * Current media player status is offered by MediaStatus
 * <p>
 * Author: xupan.shi
 * Version: V0.1 2017/3/7
 */
public class MediaBean {

    public static final String STATE_PLAYING = "PLAYING";
    public static final String STATE_PAUSED = "PAUSED";

    /**
     * media player state.
     * ONLY state PLAYING and PAUSED are available currently
     */
    private String state;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
