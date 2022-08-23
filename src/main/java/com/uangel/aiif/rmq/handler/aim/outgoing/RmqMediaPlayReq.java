package com.uangel.aiif.rmq.handler.aim.outgoing;

import com.uangel.aiif.rmq.common.RmqBuilder;
import com.uangel.aiif.rmq.handler.aim.RmqAimOutgoing;
import com.uangel.aiif.session.model.CallInfo;
import com.uangel.protobuf.Header;
import com.uangel.protobuf.MediaPlayReq;
import com.uangel.protobuf.Message;

/**
 * @author dajin kim
 */
public class RmqMediaPlayReq extends RmqAimOutgoing {

    public RmqMediaPlayReq() {
        // nothing
    }

    public boolean send(CallInfo sessionInfo, String msgType) {
        Header.Builder headerBuilder = RmqBuilder.getDefaultHeader(msgType);

        Message msg = Message.newBuilder()
                .setHeader(headerBuilder.build())
                .setMediaPlayReq(MediaPlayReq.newBuilder()
                        .setCallId(sessionInfo.getCallId())
                        .setFilePath(sessionInfo.getFilePath()))
                .build();

        return sendTo(msg);
    }

    public boolean send(int reasonCode, String reason, CallInfo sessionInfo, String msgType) {
        Header.Builder headerBuilder = RmqBuilder.getFailHeader(msgType, reason, reasonCode);

        Message msg = Message.newBuilder()
                .setHeader(headerBuilder.build())
                .setMediaPlayReq(MediaPlayReq.newBuilder()
                        .setCallId(sessionInfo.getCallId())
                        .setFilePath(sessionInfo.getFilePath()))
                .build();

        return sendTo(msg);
    }
}
