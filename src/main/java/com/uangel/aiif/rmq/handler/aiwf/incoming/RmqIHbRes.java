package com.uangel.aiif.rmq.handler.aiwf.incoming;

import com.uangel.aiif.rmq.handler.RmqIncomingMessage;
import com.uangel.protobuf.IHbRes;
import com.uangel.protobuf.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dajin kim
 */
public class RmqIHbRes extends RmqIncomingMessage<IHbRes> {
    static final Logger log = LoggerFactory.getLogger(RmqIHbRes.class);

    public RmqIHbRes(Message message) {
        super(message);
    }

    @Override
    public void handle() {
        // HB Timeout 체크

    }
}
