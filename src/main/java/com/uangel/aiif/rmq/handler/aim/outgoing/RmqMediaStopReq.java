package com.uangel.aiif.rmq.handler.aim.outgoing;

import com.uangel.aiif.rmq.common.RmqBuilder;
import com.uangel.aiif.rmq.handler.aim.RmqAimOutgoing;
import com.uangel.aiif.session.model.CallInfo;
import com.uangel.protobuf.Header;
import com.uangel.protobuf.MediaStopReq;
import com.uangel.protobuf.Message;

/**
 * @author dajin kim
 */
public class RmqMediaStopReq extends RmqAimOutgoing {

    public RmqMediaStopReq() {
        // nothing
    }

    public boolean send(CallInfo callInfo, String msgType) {
        Header.Builder headerBuilder = RmqBuilder.getDefaultHeader(msgType);

        Message msg = Message.newBuilder()
                .setHeader(headerBuilder.build())
                .setMediaStopReq(MediaStopReq.newBuilder()
                        .setCallId(callInfo.getCallId()))
                .build();

        return sendTo(msg);
    }

    public boolean send(int reasonCode, String reason, CallInfo callInfo, String msgType) {
        Header.Builder headerBuilder = RmqBuilder.getFailHeader(msgType, reason, reasonCode);

        Message msg = Message.newBuilder()
                .setHeader(headerBuilder.build())
                .setMediaStopReq(MediaStopReq.newBuilder()
                        .setCallId(callInfo.getCallId()))
                .build();

        return sendTo(msg);
    }
}
