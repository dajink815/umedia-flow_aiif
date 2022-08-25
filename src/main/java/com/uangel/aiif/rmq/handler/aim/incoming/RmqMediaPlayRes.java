package com.uangel.aiif.rmq.handler.aim.incoming;

import com.uangel.protobuf.MediaPlayRes;
import com.uangel.protobuf.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dajin kim
 */
public class RmqMediaPlayRes {
    static final Logger log = LoggerFactory.getLogger(RmqMediaPlayRes.class);

    public RmqMediaPlayRes() {
        // nothing
    }

    public void handle(Message msg) {

        MediaPlayRes res = msg.getMediaPlayRes();
        // res check isEmpty

        String callId = res.getCallId();
        int duration = res.getDuration();  // todo MediaDone Timer

        log.info("() ({}) () MediaPlayRes - Media Play Time : {} (ms)", callId, duration);
    }
}
