package com.rokid.bean;

import com.rokid.bean.response.CloudActionResponseBean;
import com.rokid.logger.Logger;

public class ActionNodeHelper {

    public static ActionNode generateActionNode(final CloudActionResponseBean cloudActionResponse) {
        ActionNode actionNode = new ActionNode();

        if (cloudActionResponse == null || !cloudActionResponse.isValid()) {
            Logger.i("cloud app response is invalid");
            return null;
        }

        actionNode.setAppId(cloudActionResponse.getAppId());
        actionNode.setActionType(cloudActionResponse.getResponse().getAction().getType());
        actionNode.setRespId(cloudActionResponse.getResponse().getRespId());
        actionNode.setResType(cloudActionResponse.getResponse().getResType());
        actionNode.setForm(cloudActionResponse.getResponse().getAction().getForm());
        actionNode.setShouldEndSession(cloudActionResponse.getResponse().getAction().isShouldEndSession());
        actionNode.setVoice(cloudActionResponse.getResponse().getAction().getVoice());
        actionNode.setMedia(cloudActionResponse.getResponse().getAction().getMedia());
        actionNode.setConfirmBean(cloudActionResponse.getResponse().getAction().getConfirm());
        actionNode.setPickup(cloudActionResponse.getResponse().getAction().getPickup());

        return actionNode;
    }




}
