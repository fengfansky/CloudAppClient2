package com.rokid.cloudappclient.parser;

import android.content.Intent;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.rokid.cloudappclient.bean.CommonResponse;
import com.rokid.cloudappclient.bean.NLPBean;
import com.rokid.cloudappclient.tts.TTSSpeakInterface;
import com.rokid.cloudappclient.util.Logger;

import java.util.Map;

/**
 * Created by fanfeng on 2017/5/8.
 */

public class IntentParser {

    private static final String KEY_NLP = "nlp";
    private static final String KEY_COMMON_RESPONSE = "extra";
    private static final String KEY_DEVICE_INFO = "device";

    TTSSpeakInterface ttsSpeakInterface;

    public IntentParser(TTSSpeakInterface ttsSpeakInterface) {
        this.ttsSpeakInterface = ttsSpeakInterface;
    }

    public void parseIntent(Intent intent) {
        if (ttsSpeakInterface == null){
            Logger.d("ttsSpeakInterface is null !");
            return;
        }
        if (intent == null) {
            Logger.d("intent null !");
            ttsSpeakInterface.speakIntentEmptyErrorTTS();
            return;
        }
        String nlp = intent.getStringExtra(KEY_NLP);
        if (TextUtils.isEmpty(nlp)) {
            Logger.d("NLP is empty!!!");
            ttsSpeakInterface.speakNLPEmptyErrorTTS();
            return;
        }

        Logger.d("parseIntent Nlp ---> ", nlp);
        NLPBean nlpBean = new Gson().fromJson(nlp, NLPBean.class);

        if (null == nlpBean) {
            Logger.d("NLPData is empty!!!");
            ttsSpeakInterface.speakNLPDataEmptyErrorTTS();
            return;
        }

        Map<String, String> slots = nlpBean.getSlots();

        if (slots == null || slots.isEmpty()) {
            Logger.i("NLP slots is invalid");
            ttsSpeakInterface.speakNLPDataEmptyErrorTTS();
            return;
        }

        if (!slots.containsKey(KEY_DEVICE_INFO)) {
            Logger.i("NLP slots has no DEVICE_INFO");
            ttsSpeakInterface.speakNLPDataEmptyErrorTTS();
            return;
        }

        if (!slots.containsKey(KEY_COMMON_RESPONSE)) {
            Logger.i("NLP slots has no COMMON_RESPONSE info");
            ttsSpeakInterface.speakNLPDataEmptyErrorTTS();
            return;
        }

        String extraString = slots.get(KEY_COMMON_RESPONSE);

        if (TextUtils.isEmpty(extraString)) {
            Logger.i("COMMON_RESPONSE info is invalid");
            ttsSpeakInterface.speakNLPDataEmptyErrorTTS();
            return;
        }

        CommonResponse commonResponse = null;

        try {
            commonResponse = new Gson().fromJson(extraString, CommonResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (null == commonResponse) {
            Logger.d("parse common response failed");
            ttsSpeakInterface.speakNLPDataEmptyErrorTTS();
            return;
        }

        ResponseParser.getInstance().parseIntentResponse(commonResponse);
    }

}
