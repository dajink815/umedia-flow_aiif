package com.uangel.aiif.rmq.handler.aiwf.incoming;

import com.uangel.protobuf.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dajin kim
 */
public class RmqDelSessionReq {
    static final Logger log = LoggerFactory.getLogger(RmqDelSessionReq.class);

    public RmqDelSessionReq() {
        // nothing
    }

    public void handle(Message msg) {
        // 세션 정리

    }
}
