package com.uangel.aiif.rmq.handler.aim.incoming;

import com.uangel.aiif.rmq.handler.RmqIncomingMessage;
import com.uangel.protobuf.MediaStopRes;
import com.uangel.protobuf.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dajin kim
 */
public class RmqMediaStopRes extends RmqIncomingMessage<MediaStopRes> {
    static final Logger log = LoggerFactory.getLogger(RmqMediaStopRes.class);

    public RmqMediaStopRes(Message message) {
        super(message);
    }

    @Override
    public void handle() {
        // Media 종료 결과 AIWF로 반환

    }
}
