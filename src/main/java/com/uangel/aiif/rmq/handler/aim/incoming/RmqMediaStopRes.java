package com.uangel.aiif.rmq.handler.aim.incoming;

import com.uangel.protobuf.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dajin kim
 */
public class RmqMediaStopRes {
    static final Logger log = LoggerFactory.getLogger(RmqMediaStopRes.class);

    public RmqMediaStopRes() {
        // nothing
    }

    public void handle(Message msg) {
        // Media 종료 결과 AIWF로 반환

    }
}
