package com.rokid.cloudappclient.parser;

import android.content.Intent;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.rokid.cloudappclient.bean.CommonResponse;
import com.rokid.cloudappclient.bean.NLPBean;
import com.rokid.cloudappclient.tts.TTSSpeakInterface;
import com.rokid.cloudappclient.util.Logger;

import java.lang.ref.WeakReference;
import java.util.Map;

/**
 * Created by fanfeng on 2017/5/8.
 */

public class IntentParser <T extends TTSSpeakInterface>{

    private static final String KEY_NLP = "nlp";
    private static final String KEY_COMMON_RESPONSE = "extra";

    WeakReference<T> ttsSpeakReference;

    public IntentParser(T ttsSpeakInterface) {
        this.ttsSpeakReference = new WeakReference<T>(ttsSpeakInterface);
    }

    public void parseIntent(Intent intent) {
        if (ttsSpeakReference == null){
            Logger.d("ttsSpeakReference is null !");
            return;
        }
        if (intent == null) {
            Logger.d("intent null !");
            if (ttsSpeakReference.get() != null){
                ttsSpeakReference.get().speakIntentEmptyErrorTTS();
            }
            return;
        }
        String nlp = intent.getStringExtra(KEY_NLP);
        if (TextUtils.isEmpty(nlp)) {
            Logger.d("NLP is empty!!!");
            if (ttsSpeakReference.get() != null){
                ttsSpeakReference.get().speakIntentEmptyErrorTTS();
            }
            return;
        }

        Logger.d("parseIntent Nlp ---> ", nlp);
        NLPBean nlpBean = new Gson().fromJson(nlp, NLPBean.class);

        if (null == nlpBean) {
            Logger.d("NLPData is empty!!!");
            if (ttsSpeakReference.get() != null){
                ttsSpeakReference.get().speakIntentEmptyErrorTTS();
            }
            return;
        }

        Map<String, String> slots = nlpBean.getSlots();

        if (slots == null || slots.isEmpty()) {
            Logger.i("NLP slots is invalid");
            if (ttsSpeakReference.get() != null){
                ttsSpeakReference.get().speakIntentEmptyErrorTTS();
            }
            return;
        }


        if (!slots.containsKey(KEY_COMMON_RESPONSE)) {
            Logger.i("NLP slots has no COMMON_RESPONSE info");
            if (ttsSpeakReference.get() != null){
                ttsSpeakReference.get().speakIntentEmptyErrorTTS();
            }
            return;
        }

        String extraString = slots.get(KEY_COMMON_RESPONSE);

        if (TextUtils.isEmpty(extraString)) {
            Logger.i("COMMON_RESPONSE info is invalid");
            if (ttsSpeakReference.get() != null){
                ttsSpeakReference.get().speakIntentEmptyErrorTTS();
            }
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
            if (ttsSpeakReference.get() != null){
                ttsSpeakReference.get().speakIntentEmptyErrorTTS();
            }
            return;
        }

        ResponseParser.getInstance().parseIntentResponse(commonResponse);
    }

}
