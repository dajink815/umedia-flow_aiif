package com.uangel.aiif.rmq.handler.aiwf.incoming;

import ai.media.stt.SttConverter;
import com.uangel.aiif.rmq.handler.RmqMsgSender;
import com.uangel.aiif.session.CallManager;
import com.uangel.aiif.session.model.CallInfo;
import com.uangel.protobuf.Header;
import com.uangel.protobuf.Message;
import com.uangel.protobuf.SttStartReq;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author dajin kim
 */
public class RmqSttStartReq {
    static final Logger log = LoggerFactory.getLogger(RmqSttStartReq.class);
    private static final CallManager callManager = CallManager.getInstance();
    private static final ScheduledExecutorService executors
            = Executors.newScheduledThreadPool(1,
            new BasicThreadFactory.Builder()
                    .namingPattern("SttStartReq-%d")
                    .daemon(true)
                    // 우선 순위 어떻게?
                    .priority(Thread.MAX_PRIORITY)
                    .build());

    public RmqSttStartReq() {
        // nothing
    }

    public void handle(Message msg) {

        Header header = msg.getHeader();
        SttStartReq req = msg.getSttStartReq();
        // req check isEmpty

        RmqMsgSender sender = RmqMsgSender.getInstance();

        String callId = req.getCallId();
        CallInfo callInfo = callManager.getCallInfo(callId);
        if (callInfo == null) {
            log.warn("() ({}) () SttStartReq Fail Find Session", callId);
            // Send Fail Response
            sender.sendSttStartRes(header.getTId(), 100, "Fail", callId);
            return;
        }

        SttConverter sttConverter = callInfo.getSttConverter();
        if (sttConverter == null) {
            log.warn("{}SttStartReq SttConverter is Null", callInfo.getLogHeader());
            // Send Fail Response
            sender.sendSttStartRes(header.getTId(), 100, "Fail", callId);
            return;
        }

        // todo STT Start

        int sttDur = req.getDuration();

        sttConverter.start();

        // RTP 수신

        // RTP 수신 결과에 따라 Response



        // Send Success Response
        sender.sendSttStartRes(header.getTId(), callInfo);

        // Schedule
        executors.schedule(() ->{

            sttConverter.stop();

            // STT 결과 처리

            // STT_Result
        }, sttDur, TimeUnit.MILLISECONDS);

    }
}
