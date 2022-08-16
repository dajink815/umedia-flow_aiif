package com.uangel.aiif.rmq.handler.aiwf;

import com.uangel.aiif.rmq.handler.RmqOutgoingMessage;
import com.uangel.aiif.service.AppInstance;

/**
 * @author dajin kim
 */
public class RmqAiwfOutgoing extends RmqOutgoingMessage {

    public RmqAiwfOutgoing() {
        super(AppInstance.getInstance().getConfig().getAiwf());
    }
}
