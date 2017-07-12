package com.rokid.cloudappclient.state;

import android.util.Log;

import com.rokid.cloudappclient.action.MediaAction;
import com.rokid.cloudappclient.bean.ActionNode;
import com.rokid.cloudappclient.bean.response.responseinfo.action.ActionBean;
import com.rokid.cloudappclient.action.VoiceAction;
import com.rokid.cloudappclient.player.ErrorPromoter;
import com.rokid.cloudappclient.util.Logger;

/**
 * Created by fanfeng on 2017/6/14.
 */

public class CutAppStateManager extends BaseAppStateManager {

    public static CutAppStateManager getInstance() {
        return AppStateManagerHolder.instance;
    }

    private static class AppStateManagerHolder {
        private static final CutAppStateManager instance = new CutAppStateManager();
    }

    @Override
    public void checkAppState() {
        Log.d("jiabin","cut checkAppState -- " + "currentMediaState:" + currentMediaState + " | currentVoiceState:" + currentVoiceState);
        super.checkAppState();
    }

    @Override
    public synchronized void onNewEventActionNode(ActionNode actionNode) {
        Log.d("jiabin","cut onNewEventActionNode ------");
        super.onNewEventActionNode(actionNode);
    }

    @Override
    public synchronized void onNewIntentActionNode(ActionNode actionNode) {
        Log.d("jiabin","cut onNewIntentActionNode --- " + "form: " + getFormType() + " | actioNode : " + actionNode);
        Logger.d("form: " + getFormType() + "onNewIntentActionNode actioNode : " + actionNode);
        if (actionNode != null) {
            this.mActionNode = actionNode;
            this.mAppId = actionNode.getAppId();
            this.shouldEndSession = actionNode.isShouldEndSession();
            MediaAction.getInstance().stopPlay();
            VoiceAction.getInstance().stopPlay();
            this.currentMediaState = null;
            this.currentVoiceState = null;
            processActionNode(actionNode);
        }else {
            promoteErrorInfo(ErrorPromoter.ERROR_TYPE.DATA_INVALID);
        }
    }

    @Override
    public synchronized void onAppPaused() {
        super.onAppPaused();
        Logger.d(" cut : pause tts and finishActivity");
        VoiceAction.getInstance().stopPlay();
        MediaAction.getInstance().stopPlay();
        finishActivity();
    }

    @Override
    public void onAppDestory() {
        super.onAppDestory();
    }

    @Override
    public String getFormType() {
        return ActionBean.FORM_CUT;
    }
}
