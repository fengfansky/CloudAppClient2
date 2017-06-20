package com.rokid.cloudappclient.reporter;

/**
 * Created by fanfeng on 2017/5/9.
 */

public class VoiceReporter extends BaseReporter {

    public static String START = "Voice.STARTED";
    public static String FINISHED = "Voice.FINISHED";

    public VoiceReporter(String event){
        super(event);
    }

    public VoiceReporter(String event, String extra) {
        super(event, extra);
    }
}
