package com.uangel.aiif.rmq.handler.aim.incoming;

import com.uangel.aiif.rmq.handler.RmqIncomingMessage;
import com.uangel.protobuf.MediaPlayRes;
import com.uangel.protobuf.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dajin kim
 */
public class RmqMediaPlayRes extends RmqIncomingMessage<MediaPlayRes> {
    static final Logger log = LoggerFactory.getLogger(RmqMediaPlayRes.class);

    public RmqMediaPlayRes(Message message) {
        super(message);
    }

    @Override
    public void handle() {

        String callId = body.getCallId();
        int duration = body.getDuration();

        log.debug("() ({}) () MediaPlayRes - Media Play Time : {} (ms)", callId, duration);
    }
}
