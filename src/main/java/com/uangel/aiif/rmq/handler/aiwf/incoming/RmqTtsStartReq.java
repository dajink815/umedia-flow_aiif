package com.uangel.aiif.rmq.handler.aiwf.incoming;

import com.uangel.protobuf.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dajin kim
 */
public class RmqTtsStartReq {
    static final Logger log = LoggerFactory.getLogger(RmqTtsStartReq.class);

    public RmqTtsStartReq() {
        // nothing
    }

    public void handle(Message msg) {

    }
}
