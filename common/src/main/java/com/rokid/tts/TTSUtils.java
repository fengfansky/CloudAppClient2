package com.rokid.tts;

/**
 * Created by fanfeng on 2017/9/11.
 */

public class TTSUtils {

    private static BaseTTSHelper ttsHelper;

    public static BaseTTSHelper getTtsHelper() {
        return ttsHelper;
    }

    public static void setTtsHelper(BaseTTSHelper ttsHelper) {
        TTSUtils.ttsHelper = ttsHelper;
    }
}
