package com.uangel.aiif.rmq.handler.aiwf.incoming;

import com.uangel.aiif.rmq.handler.RmqIncomingMessage;
import com.uangel.protobuf.Message;
import com.uangel.protobuf.TtsResultRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dajin kim
 */
public class RmqTtsResultRes extends RmqIncomingMessage<TtsResultRes> {
    static final Logger log = LoggerFactory.getLogger(RmqTtsResultRes.class);

    public RmqTtsResultRes(Message message) {
        super(message);
    }

    @Override
    public void handle() {

        // nothing
    }
}
