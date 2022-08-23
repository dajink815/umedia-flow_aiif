package com.uangel.aiif.rmq.handler.aiwf.incoming;

import ai.media.stt.SttConverter;
import com.uangel.aiif.rmq.handler.RmqMsgSender;
import com.uangel.aiif.session.CallManager;
import com.uangel.aiif.session.model.CallInfo;
import com.uangel.protobuf.Header;
import com.uangel.protobuf.MediaStartReq;
import com.uangel.protobuf.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dajin kim
 */
public class RmqSttStartReq {
    static final Logger log = LoggerFactory.getLogger(RmqSttStartReq.class);
    private static final CallManager callManager = CallManager.getInstance();

    public RmqSttStartReq() {
        // nothing
    }

    public void handle(Message msg) {

        Header header = msg.getHeader();
        MediaStartReq req = msg.getMediaStartReq();
        // req check isEmpty

        RmqMsgSender sender = RmqMsgSender.getInstance();

        String callId = req.getCallId();
        CallInfo callInfo = callManager.getCallInfo(callId);
        if (callInfo == null) {
            log.warn("() ({}) () SttStartReq Fail Find Session", callId);
            // Send Fail Response
            sender.sendSttStartRes(header.getTId(), 100, "Fail", callId);
            return;
        }

        SttConverter sttConverter = callInfo.getSttConverter();
        if (sttConverter == null) {
            log.warn("{}SttStartReq SttConverter is Null", callInfo.getLogHeader());
            // Send Fail Response
            sender.sendSttStartRes(header.getTId(), 100, "Fail", callId);
            return;
        }

        // todo STT Start


        // Send Success Response
        sender.sendSttStartRes(header.getTId(), callInfo);

    }
}
