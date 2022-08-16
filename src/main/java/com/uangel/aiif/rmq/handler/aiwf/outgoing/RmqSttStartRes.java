package com.uangel.aiif.rmq.handler.aiwf.outgoing;

import com.uangel.aiif.rmq.common.RmqBuilder;
import com.uangel.aiif.rmq.handler.aiwf.RmqAiwfOutgoing;
import com.uangel.aiif.session.model.SessionInfo;
import com.uangel.protobuf.Header;
import com.uangel.protobuf.Message;
import com.uangel.protobuf.SttStartRes;

/**
 * @author dajin kim
 */
public class RmqSttStartRes extends RmqAiwfOutgoing {

    public RmqSttStartRes() {
        // nothing
    }

    public boolean send(String tId, SessionInfo sessionInfo, String msgType) {
        Header.Builder headerBuilder = RmqBuilder.getDefaultHeader(msgType);
        headerBuilder.setTId(tId);

        Message msg = Message.newBuilder()
                .setHeader(headerBuilder.build())
                .setSttStartRes(SttStartRes.newBuilder()
                        .setCallId(sessionInfo.getCallId()))
                .build();

        return sendTo(msg);
    }

    public boolean send(String tId, int reasonCode, String reason, SessionInfo sessionInfo, String msgType) {
        Header.Builder headerBuilder = RmqBuilder.getFailHeader(msgType, reason, reasonCode);
        headerBuilder.setTId(tId);

        Message msg = Message.newBuilder()
                .setHeader(headerBuilder.build())
                .setSttStartRes(SttStartRes.newBuilder()
                        .setCallId(sessionInfo.getCallId()))
                .build();

        return sendTo(msg);
    }
}
