package com.uangel.aiif.rmq.handler.aim.outgoing;

import com.uangel.aiif.rmq.common.RmqBuilder;
import com.uangel.aiif.rmq.handler.aim.RmqAimOutgoing;
import com.uangel.aiif.session.model.SessionInfo;
import com.uangel.protobuf.Header;
import com.uangel.protobuf.MediaStartRes;
import com.uangel.protobuf.Message;

/**
 * @author dajin kim
 */
public class RmqMediaStartRes extends RmqAimOutgoing {

    public RmqMediaStartRes() {
        // nothing
    }

    public boolean send(String tId, SessionInfo sessionInfo, String msgType) {
        Header.Builder headerBuilder = RmqBuilder.getDefaultHeader(msgType);
        headerBuilder.setTId(tId);

        Message msg = Message.newBuilder()
                .setHeader(headerBuilder.build())
                .setMediaStartRes(MediaStartRes.newBuilder()
                        .setCallId(sessionInfo.getCallId())
                        // todo RTP_IP : AIIF IP
                        .setRtpPort(sessionInfo.getRtpPort()))
                .build();

        return sendTo(msg);
    }

    public boolean send(String tId, int reasonCode, String reason, SessionInfo sessionInfo, String msgType) {
        Header.Builder headerBuilder = RmqBuilder.getFailHeader(msgType, reason, reasonCode);
        headerBuilder.setTId(tId);

        Message msg = Message.newBuilder()
                .setHeader(headerBuilder.build())
                .setMediaStartRes(MediaStartRes.newBuilder()
                        .setCallId(sessionInfo.getCallId())
                        // todo RTP_IP : AIIF IP
                        .setRtpPort(sessionInfo.getRtpPort()))
                .build();

        return sendTo(msg);
    }
}
