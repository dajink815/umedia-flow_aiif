package com.uangel.aiif.rmq.handler.aiwf.incoming;

import com.uangel.aiif.rmq.handler.RmqIncomingMessage;
import com.uangel.protobuf.Message;
import com.uangel.protobuf.SttResultRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dajin kim
 */
public class RmqSttResultRes extends RmqIncomingMessage<SttResultRes> {
    static final Logger log = LoggerFactory.getLogger(RmqSttResultRes.class);

    public RmqSttResultRes(Message message) {
        super(message);
    }

    @Override
    public void handle() {

        // nothing

    }
}
