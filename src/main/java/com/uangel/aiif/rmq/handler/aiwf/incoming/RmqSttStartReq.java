package com.uangel.aiif.rmq.handler.aiwf.incoming;

import ai.media.stt.SttConverter;
import com.uangel.aiif.rmq.handler.RmqMsgSender;
import com.uangel.aiif.service.AppInstance;
import com.uangel.aiif.session.CallManager;
import com.uangel.aiif.session.model.CallInfo;
import com.uangel.protobuf.Header;
import com.uangel.protobuf.Message;
import com.uangel.protobuf.SttStartReq;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.uangel.aiif.rmq.common.RmqMsgType.*;

/**
 * @author dajin kim
 */
public class RmqSttStartReq {
    static final Logger log = LoggerFactory.getLogger(RmqSttStartReq.class);
    private static final CallManager callManager = CallManager.getInstance();
    private static final ScheduledExecutorService executors = Executors.newScheduledThreadPool(AppInstance.getInstance().getConfig().getSttThreadSize(),
            new BasicThreadFactory.Builder().namingPattern("SttStartReq-%d").build());

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
            sender.sendSttStartRes(header.getTId(), REASON_CODE_NO_SESSION, REASON_NO_SESSION, callId);
            return;
        }

        SttConverter sttConverter = callInfo.getSttConverter();
        if (sttConverter == null) {
            log.warn("{}SttStartReq SttConverter is Null", callInfo.getLogHeader());
            // Send Fail Response
            sender.sendSttStartRes(header.getTId(), REASON_CODE_AI_ERROR, REASON_AI_ERROR, callId);
            return;
        }

        log.debug("SttStartReq Credential : {}", System.getenv("GOOGLE_APPLICATION_CREDENTIALS"));

        // STT Start
        int sttDur = req.getDuration();
        sttConverter.start();         // RTP 처리 Start - sttConverter isRunning Flag True
        log.debug("{}RmqSttStartReq STT Start - Duration [{}]", callInfo.getLogHeader(), sttDur);

        // Send Success Response
        sender.sendSttStartRes(header.getTId(), callInfo);

        // Schedule
        executors.schedule(() ->{
            // STT Stop
            sttConverter.stop();    // RTP 처리 Stop - sttConverter isRunning Flag False
            // STT 결과 처리
            String result = Optional.ofNullable(sttConverter.getResultTexts()).filter(o -> !o.isEmpty()).map(o -> o.get(o.size() - 1)).orElse("");
            log.debug("{}RmqSttStartReq STT Result : {}", callInfo.getLogHeader(), result);

            // Send SttResultReq
            sender.sendSttResultReq(callInfo, result);
        }, sttDur, TimeUnit.MILLISECONDS);

    }
}
