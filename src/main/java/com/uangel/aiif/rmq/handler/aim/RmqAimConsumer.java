package com.uangel.aiif.rmq.handler.aim;


import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import com.uangel.aiif.rmq.handler.aim.incoming.RmqMediaDoneReq;
import com.uangel.aiif.rmq.handler.aim.incoming.RmqMediaPlayRes;
import com.uangel.aiif.rmq.handler.aim.incoming.RmqMediaStartReq;
import com.uangel.aiif.rmq.handler.aim.incoming.RmqMediaStopRes;
import com.uangel.protobuf.Header;
import com.uangel.protobuf.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dajin kim
 */
public class RmqAimConsumer {
    static final Logger log = LoggerFactory.getLogger(RmqAimConsumer.class);

    public void aimMessageProcessing(Message msg) {
        // For Test
        try {
            String json = JsonFormat.printer().includingDefaultValueFields().print(msg);
            //log.debug("RmqAimConsumer -->\r\n{}", json);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }

        switch(msg.getBodyCase().getNumber()){

            case Message.MEDIASTARTREQ_FIELD_NUMBER:
                RmqMediaStartReq mediaStartReq = new RmqMediaStartReq(msg);
                mediaStartReq.handle();
                break;
            case Message.MEDIAPLAYRES_FIELD_NUMBER:
                RmqMediaPlayRes mediaPlayRes = new RmqMediaPlayRes(msg);
                mediaPlayRes.handle();
                break;
            case Message.MEDIADONEREQ_FIELD_NUMBER:
                RmqMediaDoneReq mediaDoneReq = new RmqMediaDoneReq(msg);
                mediaDoneReq.handle();
                break;
            case Message.MEDIASTOPRES_FIELD_NUMBER:
                RmqMediaStopRes mediaStopRes = new RmqMediaStopRes(msg);
                mediaStopRes.handle();
                break;
            default:
                Header header = msg.getHeader();
                log.warn("Not Defined Type [{}:{}]", header.getTId(), header.getType());
                break;
        }
    }
}
