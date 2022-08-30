package com.uangel.aiif.rmq.handler.aim.incoming;

import com.uangel.aiif.rmq.handler.RmqIncomingMessage;
import com.uangel.aiif.rmq.handler.RmqMsgSender;
import com.uangel.aiif.session.CallManager;
import com.uangel.aiif.session.model.CallInfo;
import com.uangel.protobuf.MediaDoneReq;
import com.uangel.protobuf.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.uangel.aiif.rmq.common.RmqMsgType.REASON_CODE_NO_SESSION;
import static com.uangel.aiif.rmq.common.RmqMsgType.REASON_NO_SESSION;

/**
 * @author dajin kim
 */
public class RmqMediaDoneReq extends RmqIncomingMessage<MediaDoneReq> {
    static final Logger log = LoggerFactory.getLogger(RmqMediaDoneReq.class);
    private static final CallManager callManager = CallManager.getInstance();

    public RmqMediaDoneReq(Message message) {
        super(message);
    }

    @Override
    public void handle() {
        // Media Play 종료 -> AIWF 에 결과 반환

        RmqMsgSender sender = RmqMsgSender.getInstance();

        String callId = body.getCallId();
        String dialogId = body.getDialogId();
        CallInfo callInfo = callManager.getCallInfo(callId);
        if (callInfo == null) {
            log.warn("() ({}) () MediaDoneReq Fail Find Session", callId);
            // Send Fail Response
            sender.sendMediaDoneRes(getTId(), REASON_CODE_NO_SESSION, REASON_NO_SESSION, callId, dialogId);
            return;
        }

        // Send Success Response
        sender.sendMediaDoneRes(getTId(), callInfo, dialogId);

        // Send TtsResultReq
        sender.sendTtsResultReq(callInfo);
    }
}
