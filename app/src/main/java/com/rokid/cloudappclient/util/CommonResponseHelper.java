package com.rokid.cloudappclient.util;

import com.rokid.cloudappclient.bean.ActionNode;
import com.rokid.cloudappclient.bean.CommonResponseBean;
import com.rokid.cloudappclient.bean.response.CloudActionResponseBean;

public class CommonResponseHelper {

    /**
     * Method to validate the whole common response including voice and media and generate the real action object.
     *
     * @param commonResponse the given common response {@link CommonResponseBean}
     * @return the real action object. {@link ActionNode}
     */
    public static ActionNode generateActionNode(final CommonResponseBean commonResponse) {
        ActionNode actionNode = new ActionNode();

        CloudActionResponseBean cloudActionResponse = commonResponse.getAction();

        if (cloudActionResponse == null || !cloudActionResponse.isValid()) {
            Logger.i("cloud app response is invalid");
            return null;
        }

        actionNode.setAppId(cloudActionResponse.getAppId());
        actionNode.setActionType(cloudActionResponse.getResponse().getAction().getType());
        actionNode.setAsr(commonResponse.getAsr());
        actionNode.setNlp(commonResponse.getNlp());
        actionNode.setRespId(cloudActionResponse.getResponse().getRespId());
        actionNode.setResType(cloudActionResponse.getResponse().getResType());
        actionNode.setForm(cloudActionResponse.getResponse().getAction().getForm());
        actionNode.setShouldEndSession(cloudActionResponse.getResponse().getAction().isShouldEndSession());
        actionNode.setVoice(cloudActionResponse.getResponse().getAction().getVoice());
        actionNode.setMedia(cloudActionResponse.getResponse().getAction().getMedia());
        actionNode.setConfirmBean(cloudActionResponse.getResponse().getAction().getConfirm());

        return actionNode;
    }


}
