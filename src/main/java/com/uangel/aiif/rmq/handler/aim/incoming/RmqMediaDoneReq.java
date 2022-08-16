package com.uangel.aiif.rmq.handler.aim.incoming;

import com.uangel.protobuf.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dajin kim
 */
public class RmqMediaDoneReq {
    static final Logger log = LoggerFactory.getLogger(RmqMediaDoneReq.class);

    public RmqMediaDoneReq() {
        // nothing
    }

    public void handle(Message msg) {
        // Media Play 종료 -> AIWF 에 결과 반환
    }
}
