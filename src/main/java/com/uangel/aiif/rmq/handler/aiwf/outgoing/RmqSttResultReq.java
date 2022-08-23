package com.uangel.aiif.rmq.handler.aiwf.outgoing;

import com.uangel.aiif.rmq.common.RmqBuilder;
import com.uangel.aiif.rmq.handler.aiwf.RmqAiwfOutgoing;
import com.uangel.aiif.session.model.CallInfo;
import com.uangel.protobuf.Header;
import com.uangel.protobuf.Message;
import com.uangel.protobuf.SttResultReq;

/**
 * @author dajin kim
 */
public class RmqSttResultReq extends RmqAiwfOutgoing {

    public RmqSttResultReq() {
        // nothing
    }

    public boolean send(CallInfo sessionInfo, String msgType) {
        Header.Builder headerBuilder = RmqBuilder.getDefaultHeader(msgType);

        Message msg = Message.newBuilder()
                .setHeader(headerBuilder.build())
                .setSttResultReq(SttResultReq.newBuilder()
                        .setCallId(sessionInfo.getCallId())
                        .setResultText(sessionInfo.getResultTxt()))
                .build();

        return sendTo(msg);
    }

    public boolean send(int reasonCode, String reason, CallInfo sessionInfo, String msgType) {
        Header.Builder headerBuilder = RmqBuilder.getFailHeader(msgType, reason, reasonCode);

        Message msg = Message.newBuilder()
                .setHeader(headerBuilder.build())
                .setSttResultReq(SttResultReq.newBuilder()
                        .setCallId(sessionInfo.getCallId())
                        .setResultText(sessionInfo.getResultTxt()))
                .build();

        return sendTo(msg);
    }
}
