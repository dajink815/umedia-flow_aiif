package com.uangel.aiif.rmq.handler.aim;

import com.uangel.aiif.rmq.handler.RmqOutgoingMessage;
import com.uangel.aiif.service.AppInstance;

/**
 * @author dajin kim
 */
public class RmqAimOutgoing extends RmqOutgoingMessage {

    public RmqAimOutgoing() {
        super(AppInstance.getInstance().getConfig().getAim());
    }
}
