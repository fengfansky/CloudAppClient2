package com.rokid.cloudappclient.state;

import com.rokid.cloudappclient.bean.ActionNode;

/**
 * Created by fanfeng on 2017/6/14.
 */

public interface AppStateCallback {

    void onNewIntentActionNode(ActionNode actionNode);

    void onNewEventActionNode(ActionNode actionNode);

    ActionNode getCurrentActionNode();

    void onAppPaused();

    void onAppResume();
}
