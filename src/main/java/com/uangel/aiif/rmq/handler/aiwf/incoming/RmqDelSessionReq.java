package com.uangel.aiif.rmq.handler.aiwf.incoming;

import com.uangel.aiif.rmq.handler.RmqMsgSender;
import com.uangel.aiif.session.CallManager;
import com.uangel.protobuf.DelSessionReq;
import com.uangel.protobuf.Header;
import com.uangel.protobuf.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dajin kim
 */
public class RmqDelSessionReq {
    static final Logger log = LoggerFactory.getLogger(RmqDelSessionReq.class);
    private static final CallManager callManager = CallManager.getInstance();

    public RmqDelSessionReq() {
        // nothing
    }

    public void handle(Message msg) {

        Header header = msg.getHeader();
        DelSessionReq req = msg.getDelSessionReq();
        // req check isEmpty

        // Delete Session
        String callId = req.getCallId();
        callManager.deleteCallInfo(callId);

        // Send Success Response
        RmqMsgSender sender = RmqMsgSender.getInstance();
        sender.sendDelSessionRes(header.getTId(), callId);
    }
}
