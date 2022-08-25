package com.uangel.aiif.rmq.handler.aiwf;


import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import com.uangel.aiif.rmq.handler.aiwf.incoming.*;
import com.uangel.protobuf.Header;
import com.uangel.protobuf.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dajin kim
 */
public class RmqAiwfConsumer {
    static final Logger log = LoggerFactory.getLogger(RmqAiwfConsumer.class);

    public void aiwfMessageProcessing(Message msg) {
        // For Test
        try {
            String json = JsonFormat.printer().includingDefaultValueFields().print(msg);
            //log.debug("RmqAiwfConsumer -->\r\n{}", json);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }

        switch(msg.getBodyCase().getNumber()){
            case Message.IHBRES_FIELD_NUMBER:
                RmqIHbRes iHbRes = new RmqIHbRes();
                iHbRes.handle(msg);
                break;
            case Message.CREATESESSIONREQ_FIELD_NUMBER:
                RmqCreateSessionReq createSessionReq = new RmqCreateSessionReq();
                createSessionReq.handle(msg);
                break;
            case Message.DELSESSIONREQ_FIELD_NUMBER:
                RmqDelSessionReq delSessionReq = new RmqDelSessionReq();
                delSessionReq.handle(msg);
                break;
            case Message.TTSSTARTREQ_FIELD_NUMBER:
                RmqTtsStartReq ttsStartReq = new RmqTtsStartReq();
                ttsStartReq.handle(msg);
                break;
            case Message.TTSRESULTRES_FIELD_NUMBER:
                RmqTtsResultRes ttsResultRes = new RmqTtsResultRes();
                ttsResultRes.handle(msg);
                break;
            case Message.STTSTARTREQ_FIELD_NUMBER:
                RmqSttStartReq sttStartReq = new RmqSttStartReq();
                sttStartReq.handle(msg);
                break;
            case Message.STTRESULTRES_FIELD_NUMBER:
                RmqSttResultRes sttResultRes = new RmqSttResultRes();
                sttResultRes.handle(msg);
                break;
            default:
                Header header = msg.getHeader();
                log.warn("Not Defined Type [{}:{}]", header.getTId(), header.getType());
                break;
        }
    }
}
