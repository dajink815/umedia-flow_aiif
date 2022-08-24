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

    public boolean send(CallInfo callInfo, String resultTxt, String msgType) {
        Header.Builder headerBuilder = RmqBuilder.getDefaultHeader(msgType);

        Message msg = Message.newBuilder()
                .setHeader(headerBuilder.build())
                .setSttResultReq(SttResultReq.newBuilder()
                        .setCallId(callInfo.getCallId())
                        .setResultText(resultTxt))
                .build();

        return sendTo(msg);
    }

    public boolean send(int reasonCode, String reason, CallInfo callInfo, String resultTxt, String msgType) {
        Header.Builder headerBuilder = RmqBuilder.getFailHeader(msgType, reason, reasonCode);

        Message msg = Message.newBuilder()
                .setHeader(headerBuilder.build())
                .setSttResultReq(SttResultReq.newBuilder()
                        .setCallId(callInfo.getCallId())
                        .setResultText(resultTxt))
                .build();

        return sendTo(msg);
    }
}
