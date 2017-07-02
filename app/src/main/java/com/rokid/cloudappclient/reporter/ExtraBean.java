package com.rokid.cloudappclient.reporter;

import com.rokid.cloudappclient.util.Logger;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by fanfeng on 2017/6/29.
 */

public class ExtraBean {

    public static final String EXTRA_TOKEN = "MEDIA.TOKEN";
    public static final String EXTRA_PROGRESS = "MEDIA.PROGRESS";
    public static final String EXTRA_DURATION = "MEDIA.DURATION";


    private Map<String,String> extraMap;

    private ExtraBean(Builder builder) {
        extraMap = new HashMap<>();
        extraMap.put(EXTRA_TOKEN, builder.token);
        extraMap.put(EXTRA_DURATION, builder.progress);
        extraMap.put(EXTRA_PROGRESS, builder.duration);
    }

    @Override
    public String toString() {
        if (extraMap == null || extraMap.isEmpty()){
            return null;
        }
        Logger.d("event extra : " + extraMap.toString());
        return extraMap.toString();
    }

    public static final class Builder {
        private String token;
        private String progress;
        private String duration;

        public Builder() {
        }

        public Builder token(String val) {
            token = val;
            return this;
        }

        public Builder progress(String val) {
            progress = val;
            return this;
        }

        public Builder duration(String val) {
            duration = val;
            return this;
        }

        public ExtraBean build() {
            return new ExtraBean(this);
        }
    }
}
