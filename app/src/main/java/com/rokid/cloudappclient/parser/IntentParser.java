package com.rokid.cloudappclient.parser;

import android.content.Intent;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.rokid.cloudappclient.bean.CommonResponseBean;
import com.rokid.cloudappclient.bean.NLPBean;
import com.rokid.cloudappclient.player.ErrorPromoter;
import com.rokid.cloudappclient.util.Logger;

import java.io.IOException;
import java.util.Map;

/**
 * Created by fanfeng on 2017/5/8.
 */

public class IntentParser {

    private static final String KEY_NLP = "nlp";
    private static final String KEY_COMMON_RESPONSE = "extra";

    public void parseIntent(Intent intent) throws IOException {

        if (intent == null) {
            Logger.d("intent null !");
            ErrorPromoter.getInstance().speakErrorPromote(ErrorPromoter.ERROR_TYPE.DATA_INVALID,null);
            return;
        }
        String nlp = intent.getStringExtra(KEY_NLP);
        if (TextUtils.isEmpty(nlp)) {
            Logger.d("NLP is empty!!!");
            ErrorPromoter.getInstance().speakErrorPromote(ErrorPromoter.ERROR_TYPE.DATA_INVALID, null);
            return;
        }

        Logger.d("parseIntent Nlp ---> ", nlp);
        NLPBean nlpBean = new Gson().fromJson(nlp, NLPBean.class);

        if (null == nlpBean) {
            Logger.d("NLPData is empty!!!");
            ErrorPromoter.getInstance().speakErrorPromote(ErrorPromoter.ERROR_TYPE.DATA_INVALID, null);
            return;
        }

        Map<String, String> slots = nlpBean.getSlots();

        if (slots == null || slots.isEmpty()) {
            Logger.i("NLP slots is invalid");
            ErrorPromoter.getInstance().speakErrorPromote(ErrorPromoter.ERROR_TYPE.DATA_INVALID, null);
            return;
        }


        if (!slots.containsKey(KEY_COMMON_RESPONSE)) {
            Logger.i("NLP slots has no COMMON_RESPONSE info");
            ErrorPromoter.getInstance().speakErrorPromote(ErrorPromoter.ERROR_TYPE.DATA_INVALID, null);
            return;
        }

        String extraString = slots.get(KEY_COMMON_RESPONSE);

        if (TextUtils.isEmpty(extraString)) {
            Logger.i("COMMON_RESPONSE info is invalid");
            ErrorPromoter.getInstance().speakErrorPromote(ErrorPromoter.ERROR_TYPE.DATA_INVALID, null);
            return;
        }

        CommonResponseBean commonResponse = null;

        try {
            commonResponse = new Gson().fromJson(extraString, CommonResponseBean.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (null == commonResponse) {
            Logger.d("parse common response failed");
            ErrorPromoter.getInstance().speakErrorPromote(ErrorPromoter.ERROR_TYPE.DATA_INVALID, null);
            return;
        }

        ResponseParser.getInstance().parseIntentResponse(commonResponse);
    }

}
