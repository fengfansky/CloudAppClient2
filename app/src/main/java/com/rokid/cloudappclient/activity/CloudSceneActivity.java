package com.rokid.cloudappclient.activity;

import com.rokid.cloudappclient.state.BaseAppStateManager;
import com.rokid.cloudappclient.state.SceneAppStateManager;

public class CloudSceneActivity extends BaseActivity {

    @Override
    public BaseAppStateManager getAppStateManager() {
        return SceneAppStateManager.getInstance();
    }

}
