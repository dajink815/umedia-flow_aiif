package com.uangel.aiif.rmq.handler.aiwf.outgoing;

import com.uangel.aiif.rmq.common.RmqBuilder;
import com.uangel.aiif.rmq.handler.aiwf.RmqAiwfOutgoing;
import com.uangel.aiif.session.model.SessionInfo;
import com.uangel.protobuf.Header;
import com.uangel.protobuf.Message;
import com.uangel.protobuf.TtsResultReq;

/**
 * @author dajin kim
 */
public class RmqTtsResultReq extends RmqAiwfOutgoing {

    public RmqTtsResultReq() {
        // nothing
    }

    public boolean send(SessionInfo sessionInfo, String msgType) {
        Header.Builder headerBuilder = RmqBuilder.getDefaultHeader(msgType);

        Message msg = Message.newBuilder()
                .setHeader(headerBuilder.build())
                .setTtsResultReq(TtsResultReq.newBuilder()
                        .setCallId(sessionInfo.getCallId()))
                .build();

        return sendTo(msg);
    }

    public boolean send(int reasonCode, String reason, SessionInfo sessionInfo, String msgType) {
        Header.Builder headerBuilder = RmqBuilder.getFailHeader(msgType, reason, reasonCode);

        Message msg = Message.newBuilder()
                .setHeader(headerBuilder.build())
                .setTtsResultReq(TtsResultReq.newBuilder()
                        .setCallId(sessionInfo.getCallId()))
                .build();

        return sendTo(msg);
    }
}
