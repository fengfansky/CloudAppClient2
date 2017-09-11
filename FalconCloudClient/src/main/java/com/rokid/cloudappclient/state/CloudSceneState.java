package com.rokid.cloudappclient.state;


import com.rokid.bean.ActionNode;
import com.rokid.logger.Logger;
import com.rokid.monitor.CloudSceneStateMonitor;
import com.rokid.parser.ResponseParser;

import java.io.IOException;

import rokid.context.SceneState;
import rokid.event.KeyInputEvent;

public class CloudSceneState extends SceneState {

    public CloudSceneState() {
        super();
    }

    @Override
    public void onStateResume() {
        super.onStateResume();
        CloudSceneStateMonitor.getInstance().onResume();
    }

    @Override
    public void onStatePause() {
        super.onStatePause();
        CloudSceneStateMonitor.getInstance().onPause();
    }

    @Override
    public void onStateCreate() {
        super.onStateCreate();
    }

    @Override
    public void onStateDestroy() {
        super.onStateDestroy();
        CloudSceneStateMonitor.getInstance().onDestroy();
    }

    @Override
    public void onNlpMessage(String nlp, String asr, String action) {
        super.onNlpMessage(nlp, asr, action);
        ActionNode actionNode = null;

        try {
            actionNode = ResponseParser.getInstance().parseAction(action);
        } catch (IOException e) {
            Logger.e(" speak error info exception !");
            e.printStackTrace();
        }
        CloudSceneStateMonitor.getInstance().onNewIntentActionNode(actionNode);
    }

    @Override
    public boolean onKeyInputEvent(KeyInputEvent event) {
        return super.onKeyInputEvent(event);
    }

    public CloudSceneStateMonitor getCloudStateMonitor() {
        return CloudSceneStateMonitor.getInstance();
    }
}
