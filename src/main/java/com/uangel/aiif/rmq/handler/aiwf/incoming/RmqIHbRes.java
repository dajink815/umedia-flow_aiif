package com.uangel.aiif.rmq.handler.aiwf.incoming;

import com.uangel.protobuf.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dajin kim
 */
public class RmqIHbRes {
    static final Logger log = LoggerFactory.getLogger(RmqIHbRes.class);

    public RmqIHbRes() {
        // nothing
    }

    public void handle(Message msg) {
        // HB Timeout 체크

    }
}