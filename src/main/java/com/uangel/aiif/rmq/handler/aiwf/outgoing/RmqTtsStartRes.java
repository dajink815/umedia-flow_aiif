package com.uangel.aiif.rmq.handler.aiwf.outgoing;

import com.uangel.aiif.rmq.common.RmqBuilder;
import com.uangel.aiif.rmq.handler.aiwf.RmqAiwfOutgoing;
import com.uangel.aiif.session.model.SessionInfo;
import com.uangel.protobuf.Header;
import com.uangel.protobuf.Message;
import com.uangel.protobuf.TtsStartRes;

/**
 * @author dajin kim
 */
public class RmqTtsStartRes extends RmqAiwfOutgoing {

    public RmqTtsStartRes() {
        // nothing
    }

    public boolean send(String tId, SessionInfo sessionInfo, String msgType) {
        Header.Builder headerBuilder = RmqBuilder.getDefaultHeader(msgType);
        headerBuilder.setTId(tId);

        Message msg = Message.newBuilder()
                .setHeader(headerBuilder.build())
                .setTtsStartRes(TtsStartRes.newBuilder()
                        .setCallId(sessionInfo.getCallId()))
                .build();

        return sendTo(msg);
    }

    public boolean send(String tId, int reasonCode, String reason, SessionInfo sessionInfo, String msgType) {
        Header.Builder headerBuilder = RmqBuilder.getFailHeader(msgType, reason, reasonCode);
        headerBuilder.setTId(tId);

        Message msg = Message.newBuilder()
                .setHeader(headerBuilder.build())
                .setTtsStartRes(TtsStartRes.newBuilder()
                        .setCallId(sessionInfo.getCallId()))
                .build();

        return sendTo(msg);
    }
}
