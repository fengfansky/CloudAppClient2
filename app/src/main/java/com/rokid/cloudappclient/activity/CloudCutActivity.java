package com.rokid.cloudappclient.activity;

import com.rokid.cloudappclient.state.BaseAppStateManager;
import com.rokid.cloudappclient.state.CutAppStateManager;

public class CloudCutActivity extends BaseActivity {

    @Override
    public BaseAppStateManager getAppStateManager() {
        return CutAppStateManager.getInstance();
    }

    @Override
    public void onInvalidateState() {
        finish();
    }

    @Override
    public void onAllTaskFinished() {
        finish();
    }
}
