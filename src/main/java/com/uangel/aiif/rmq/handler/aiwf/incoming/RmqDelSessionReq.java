package com.uangel.aiif.rmq.handler.aiwf.incoming;

import com.uangel.aiif.rmq.handler.RmqIncomingMessage;
import com.uangel.aiif.rmq.handler.RmqMsgSender;
import com.uangel.aiif.session.CallManager;
import com.uangel.aiif.session.model.CallInfo;
import com.uangel.protobuf.DelSessionReq;
import com.uangel.protobuf.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.uangel.aiif.rmq.common.RmqMsgType.REASON_CODE_NO_SESSION;
import static com.uangel.aiif.rmq.common.RmqMsgType.REASON_NO_SESSION;

/**
 * @author dajin kim
 */
public class RmqDelSessionReq extends RmqIncomingMessage<DelSessionReq> {
    static final Logger log = LoggerFactory.getLogger(RmqDelSessionReq.class);
    private static final CallManager callManager = CallManager.getInstance();

    public RmqDelSessionReq(Message message) {
        super(message);
    }

    @Override
    public void handle() {

        RmqMsgSender sender = RmqMsgSender.getInstance();

        // Delete Session
        String callId = body.getCallId();
        CallInfo callInfo = callManager.getCallInfo(callId);
        if (callInfo == null) {
            log.warn("() ({}) () DelSessionReq Fail Find Session", callId);
            // Send Fail Response
            sender.sendDelSessionRes(getTId(), REASON_CODE_NO_SESSION, REASON_NO_SESSION, callId);
            return;
        }

        callInfo.handleLock(() -> {
            log.debug("{}DelSessionReq - CallInfo Lock", callInfo.getLogHeader());
            callInfo.setClearing(true);
            callManager.deleteCallInfo(callId);
        });

        // Send Success Response
        sender.sendDelSessionRes(getTId(), callId);
    }
}
