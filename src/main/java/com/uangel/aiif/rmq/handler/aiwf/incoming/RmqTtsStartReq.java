package com.uangel.aiif.rmq.handler.aiwf.incoming;

import ai.media.tts.TtsConverter;
import com.uangel.aiif.ai.google.tts.TtsType;
import com.uangel.aiif.rmq.handler.RmqMsgSender;
import com.uangel.aiif.session.CallManager;
import com.uangel.aiif.session.model.CallInfo;
import com.uangel.protobuf.Header;
import com.uangel.protobuf.Message;
import com.uangel.protobuf.TtsStartReq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dajin kim
 */
public class RmqTtsStartReq {
    static final Logger log = LoggerFactory.getLogger(RmqTtsStartReq.class);
    private static final CallManager callManager = CallManager.getInstance();

    public RmqTtsStartReq() {
        // nothing
    }

    public void handle(Message msg) {

        Header header = msg.getHeader();
        TtsStartReq req = msg.getTtsStartReq();
        // req check isEmpty

        RmqMsgSender sender = RmqMsgSender.getInstance();

        String callId = req.getCallId();
        CallInfo callInfo = callManager.getCallInfo(callId);
        if (callInfo == null) {
            log.warn("() ({}) () TtsStartReq Fail Find Session", callId);
            // Send Fail Response
            sender.sendTtsStartRes(header.getTId(), 100, "Fail", callId);
            return;
        }

        TtsConverter ttsConverter = callInfo.getTtsConverter();
        if (ttsConverter == null) {
            log.warn("{}TtsStartReq TtsConverter is Null", callInfo.getLogHeader());
            // Send Fail Response
            sender.sendTtsStartRes(header.getTId(), 100, "Fail", callId);
            return;
        }

        // TTS Start
        TtsType ttsType = TtsType.getTypeEnum(req.getType());
        String content = req.getContent();

        if (TtsType.FILE.equals(ttsType)) {


        } else if (TtsType.MENT.equals(ttsType)) {


        } else {
            log.warn("{} Unknown TTS Type : {}", callInfo.getLogHeader(), ttsType);
        }


        // Send Success Response
        sender.sendTtsStartRes(header.getTId(), callInfo);
    }
}
