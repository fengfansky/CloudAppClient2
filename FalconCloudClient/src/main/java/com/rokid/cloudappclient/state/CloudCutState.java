package com.rokid.cloudappclient.state;

import com.rokid.bean.ActionNode;
import com.rokid.logger.Logger;
import com.rokid.monitor.CloudCutStateMonitor;
import com.rokid.parser.ResponseParser;

import java.io.IOException;

import rokid.context.CutState;
import rokid.event.KeyInputEvent;

public class CloudCutState extends CutState {
    public CloudCutState() {
        super();
    }

    @Override
    public void onStateResume() {
        super.onStateResume();
        CloudCutStateMonitor.getInstance().onResume();
    }

    @Override
    public void onStatePause() {
        super.onStatePause();
        CloudCutStateMonitor.getInstance().onPause();
    }

    @Override
    public void onStateCreate() {
        super.onStateCreate();
    }

    @Override
    public void onStateDestroy() {
        super.onStateDestroy();
        CloudCutStateMonitor.getInstance().onDestroy();
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
        CloudCutStateMonitor.getInstance().onNewIntentActionNode(actionNode);
    }

    @Override
    public boolean onKeyInputEvent(KeyInputEvent event) {
        return super.onKeyInputEvent(event);
    }

    public CloudCutStateMonitor getCloudStateMonitor() {
        return CloudCutStateMonitor.getInstance();
    }

}
