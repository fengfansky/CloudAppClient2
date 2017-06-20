package com.rokid.cloudappclient.reporter;

/**
 * Created by fanfeng on 2017/5/9.
 */

public class MediaReporter extends BaseReporter {

    public static final String START = "Media.START_PLAYING";
    public static final String PAUSED = "Media.PAUSED";
    public static final String FINISHED = "Media.FINISHED";
    public static final String NEAR_FINISH = "Media.NEAR_FINISH";
    public static final String ERROR = "Media.ERROR";

    public MediaReporter(String event){
        super(event);
    }

    public MediaReporter(String event, String extra) {
        super(event, extra);
    }
}
