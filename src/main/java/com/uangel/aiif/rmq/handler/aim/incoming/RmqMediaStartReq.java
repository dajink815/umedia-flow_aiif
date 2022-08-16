package com.uangel.aiif.rmq.handler.aim.incoming;

import com.uangel.protobuf.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dajin kim
 */
public class RmqMediaStartReq {
    static final Logger log = LoggerFactory.getLogger(RmqMediaStartReq.class);

    public RmqMediaStartReq() {
        // nothing
    }

    public void handle(Message msg) {
        // RTP Port 할당 -> AIM 전달

    }
}
