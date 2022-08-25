package com.uangel.aiif.rmq.handler.aim.outgoing;

import com.uangel.aiif.rmq.common.RmqBuilder;
import com.uangel.aiif.rmq.handler.aim.RmqAimOutgoing;
import com.uangel.aiif.service.AppInstance;
import com.uangel.aiif.session.model.CallInfo;
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

    public boolean send(String tId, CallInfo callInfo, String msgType) {
        Header.Builder headerBuilder = RmqBuilder.getDefaultHeader(msgType);
        headerBuilder.setTId(tId);

        Message msg = Message.newBuilder()
                .setHeader(headerBuilder.build())
                .setMediaStartRes(MediaStartRes.newBuilder()
                        .setCallId(callInfo.getCallId())
                        .setRtpIp(AppInstance.getInstance().getConfig().getServerIp())
                        .setRtpPort(callInfo.getRtpPort()))
                .build();

        return sendTo(msg);
    }

    public boolean send(String tId, int reasonCode, String reason, CallInfo callInfo, String msgType) {
        Header.Builder headerBuilder = RmqBuilder.getFailHeader(msgType, reason, reasonCode);
        headerBuilder.setTId(tId);

        Message msg = Message.newBuilder()
                .setHeader(headerBuilder.build())
                .setMediaStartRes(MediaStartRes.newBuilder()
                        .setCallId(callInfo.getCallId())
                        .setRtpIp(AppInstance.getInstance().getConfig().getServerIp())
                        .setRtpPort(callInfo.getRtpPort()))
                .build();

        return sendTo(msg);
    }

    public boolean send(String tId, int reasonCode, String reason, String callId, String msgType) {
        Header.Builder headerBuilder = RmqBuilder.getFailHeader(msgType, reason, reasonCode);
        headerBuilder.setTId(tId);

        Message msg = Message.newBuilder()
                .setHeader(headerBuilder.build())
                .setMediaStartRes(MediaStartRes.newBuilder()
                        .setCallId(callId))
                .build();

        return sendTo(msg);
    }
}
