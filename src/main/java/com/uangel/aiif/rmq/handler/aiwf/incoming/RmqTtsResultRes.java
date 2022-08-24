package com.uangel.aiif.rmq.handler.aiwf.incoming;

import com.uangel.protobuf.Header;
import com.uangel.protobuf.Message;
import com.uangel.protobuf.TtsResultRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dajin kim
 */
public class RmqTtsResultRes {
    static final Logger log = LoggerFactory.getLogger(RmqTtsResultRes.class);

    public RmqTtsResultRes() {
        // nothing
    }

    public void handle(Message msg) {

        Header header = msg.getHeader();
        TtsResultRes res = msg.getTtsResultRes();
        // res check isEmpty

        String callId = res.getCallId();

        //
    }
}
