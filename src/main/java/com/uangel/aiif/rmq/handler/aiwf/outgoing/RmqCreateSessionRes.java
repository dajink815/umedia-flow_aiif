package com.uangel.aiif.rmq.handler.aiwf.outgoing;

import com.uangel.aiif.rmq.common.RmqBuilder;
import com.uangel.aiif.rmq.handler.aiwf.RmqAiwfOutgoing;
import com.uangel.aiif.session.model.SessionInfo;
import com.uangel.protobuf.CreateSessionRes;
import com.uangel.protobuf.Header;
import com.uangel.protobuf.Message;

/**
 * @author dajin kim
 */
public class RmqCreateSessionRes extends RmqAiwfOutgoing {

    public RmqCreateSessionRes() {
        // nothing
    }

    public boolean send(String tId, SessionInfo sessionInfo, String msgType) {
        Header.Builder headerBuilder = RmqBuilder.getDefaultHeader(msgType);
        headerBuilder.setTId(tId);

        Message msg = Message.newBuilder()
                .setHeader(headerBuilder.build())
                .setCreateSessionRes(CreateSessionRes.newBuilder()
                        .setCallId(sessionInfo.getCallId()))
                .build();

        return sendTo(msg);
    }

    public boolean send(String tId, int reasonCode, String reason, String callId, String msgType) {
        Header.Builder headerBuilder = RmqBuilder.getFailHeader(msgType, reason, reasonCode);
        headerBuilder.setTId(tId);

        Message msg = Message.newBuilder()
                .setHeader(headerBuilder.build())
                .setCreateSessionRes(CreateSessionRes.newBuilder()
                        .setCallId(callId))
                .build();

        return sendTo(msg);
    }
}
