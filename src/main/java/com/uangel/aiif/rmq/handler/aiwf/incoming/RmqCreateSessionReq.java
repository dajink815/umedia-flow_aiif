package com.uangel.aiif.rmq.handler.aiwf.incoming;

import com.uangel.aiif.rmq.handler.RmqMsgSender;
import com.uangel.aiif.session.CallManager;
import com.uangel.aiif.session.model.CallInfo;
import com.uangel.protobuf.CreateSessionReq;
import com.uangel.protobuf.Header;
import com.uangel.protobuf.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dajin kim
 */
public class RmqCreateSessionReq {
    static final Logger log = LoggerFactory.getLogger(RmqCreateSessionReq.class);
    private static final CallManager callManager = CallManager.getInstance();

    public RmqCreateSessionReq() {
        // nothing
    }

    public void handle(Message msg) {

        Header header = msg.getHeader();
        CreateSessionReq req = msg.getCreateSessionReq();
        // req check isEmpty

        RmqMsgSender sender = RmqMsgSender.getInstance();

        String callId = req.getCallId();
        CallInfo callInfo = callManager.createCallInfo(callId);
        if (callInfo == null) {
            log.warn("() ({}) () Fail Create Session", callId);
            // Send Fail Response
            sender.sendCreateSessionRes(header.getTId(), 100, "Fail", callId);
            return;
        }

        // RTP Port 할당
        int rtpPort = 5050;

        // 할당 실패
        // Send Fail Response
        // sender.sendCreateSessionRes(header.getTId(), 100, "Fail", callId);

        callInfo.setRtpPort(rtpPort);

        // Send Success Response
        sender.sendCreateSessionRes(header.getTId(), callInfo);
    }
}
