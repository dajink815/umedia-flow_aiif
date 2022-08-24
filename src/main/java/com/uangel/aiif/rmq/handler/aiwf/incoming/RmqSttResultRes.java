package com.uangel.aiif.rmq.handler.aiwf.incoming;

import com.uangel.protobuf.Header;
import com.uangel.protobuf.Message;
import com.uangel.protobuf.SttResultRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dajin kim
 */
public class RmqSttResultRes {
    static final Logger log = LoggerFactory.getLogger(RmqSttResultRes.class);

    public RmqSttResultRes() {
        // nothing
    }

    public void handle(Message msg) {

        Header header = msg.getHeader();
        SttResultRes res = msg.getSttResultRes();
        // res check isEmpty

        String callId = res.getCallId();

        //

    }
}
