package com.uangel.aiif.rmq.handler.aiwf.incoming;

import com.uangel.aiif.rmq.handler.RmqIncomingMessage;
import com.uangel.aiif.rmq.handler.RmqMsgSender;
import com.uangel.aiif.session.CallManager;
import com.uangel.aiif.session.model.CallInfo;
import com.uangel.protobuf.CreateSessionReq;
import com.uangel.protobuf.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.uangel.aiif.rmq.common.RmqMsgType.REASON_CODE_NO_SESSION;
import static com.uangel.aiif.rmq.common.RmqMsgType.REASON_NO_SESSION;

/**
 * @author dajin kim
 */
public class RmqCreateSessionReq extends RmqIncomingMessage<CreateSessionReq> {
    static final Logger log = LoggerFactory.getLogger(RmqCreateSessionReq.class);
    private static final CallManager callManager = CallManager.getInstance();

    public RmqCreateSessionReq(Message message) {
        super(message);
    }

    @Override
    public void handle() {

        RmqMsgSender sender = RmqMsgSender.getInstance();

        // Create Session
        String callId = body.getCallId();
        CallInfo callInfo = callManager.createCallInfo(callId);
        if (callInfo == null) {
            log.warn("() ({}) () CreateSessionReq Fail Create Session", callId);
            // Send Fail Response
            sender.sendCreateSessionRes(getTId(), REASON_CODE_NO_SESSION, REASON_NO_SESSION, callId);
            return;
        }

        // RTP 할당은 CallManager.createCallInfo() 메서드에서 처리됨

        // Send Success Response
        sender.sendCreateSessionRes(getTId(), callInfo);
    }
}
