package com.rokid.cloudappclient.activity;

import com.rokid.cloudappclient.state.BaseAppStateManager;
import com.rokid.cloudappclient.state.SceneAppStateManager;
import com.rokid.cloudappclient.util.Logger;

public class CloudSceneActivity extends BaseActivity {

    @Override
    public BaseAppStateManager getAppStateManager() {
        return SceneAppStateManager.getInstance();
    }

    @Override
    public void onInvalidateState() {

    }

    @Override
    public void onAllTaskFinished() {
        Logger.d(" scene don't finish app ");
    }
}
