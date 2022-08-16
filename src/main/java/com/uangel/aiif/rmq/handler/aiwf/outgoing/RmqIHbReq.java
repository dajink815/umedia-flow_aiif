package com.uangel.aiif.rmq.handler.aiwf.outgoing;

import com.uangel.aiif.rmq.common.RmqBuilder;
import com.uangel.aiif.rmq.handler.aiwf.RmqAiwfOutgoing;
import com.uangel.protobuf.Header;
import com.uangel.protobuf.IHbReq;
import com.uangel.protobuf.Message;

/**
 * @author dajin kim
 */
public class RmqIHbReq extends RmqAiwfOutgoing {

    public RmqIHbReq() {
        // nothing
    }

    public boolean send(int status, String msgType) {
        Header.Builder headerBuilder = RmqBuilder.getDefaultHeader(msgType);

        Message msg = Message.newBuilder()
                .setHeader(headerBuilder.build())
                .setIHbReq(IHbReq.newBuilder()
                        .setStatus(status))
                .build();

        return sendTo(msg);

    }
}
