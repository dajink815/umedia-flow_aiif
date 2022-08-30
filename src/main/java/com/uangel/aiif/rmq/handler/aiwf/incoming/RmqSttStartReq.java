package com.uangel.aiif.rmq.handler.aiwf.incoming;

import ai.media.stt.SttConverter;
import com.uangel.aiif.rmq.handler.RmqIncomingMessage;
import com.uangel.aiif.rmq.handler.RmqMsgSender;
import com.uangel.aiif.service.AppInstance;
import com.uangel.aiif.session.CallManager;
import com.uangel.aiif.session.model.CallInfo;
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
public class RmqSttStartReq extends RmqIncomingMessage<SttStartReq> {
    static final Logger log = LoggerFactory.getLogger(RmqSttStartReq.class);
    private static final CallManager callManager = CallManager.getInstance();
    private static final ScheduledExecutorService executors = Executors.newScheduledThreadPool(AppInstance.getInstance().getConfig().getSttThreadSize(),
            new BasicThreadFactory.Builder().namingPattern("SttStartReq-%d").build());

    public RmqSttStartReq(Message message) {
        super(message);
    }

    @Override
    public void handle() {

        RmqMsgSender sender = RmqMsgSender.getInstance();

        String callId = body.getCallId();
        String dialogId = body.getDialogId();
        CallInfo callInfo = callManager.getCallInfo(callId);
        if (callInfo == null) {
            log.warn("() ({}) () SttStartReq Fail Find Session", callId);
            // Send Fail Response
            sender.sendSttStartRes(getTId(), REASON_CODE_NO_SESSION, REASON_NO_SESSION, callId, dialogId);
            return;
        }

        SttConverter sttConverter = callInfo.getSttConverter();
        if (sttConverter == null) {
            log.warn("{}SttStartReq SttConverter is Null", callInfo.getLogHeader());
            // Send Fail Response
            sender.sendSttStartRes(getTId(), REASON_CODE_AI_ERROR, REASON_AI_ERROR, callId, dialogId);
            return;
        }

        // Clearing 체크
        try {
            log.debug("{}SttStartReq - CallInfo Lock", callInfo.getLogHeader());
            callInfo.lock();

            if (callInfo.isClearing()) {
                log.warn("() ({}) () SttStartReq Session is Clearing", callId);
                // Send Fail Response
                sender.sendSttStartRes(getTId(), REASON_CODE_NO_SESSION, REASON_NO_SESSION, callId, dialogId);
                return;
            }

        } finally {
            callInfo.unlock();
        }

        callInfo.setSttDialogId(dialogId);
        log.debug("SttStartReq Credential : {}", System.getenv("GOOGLE_APPLICATION_CREDENTIALS"));

        // STT Start
        int sttDur = body.getDuration();
        log.debug("{}SttStartReq STT Start - Duration [{}], SttConverter [{}]", callInfo.getLogHeader(), sttDur, sttConverter.hashCode());

        try {
            sttConverter.start();         // RTP 처리 Start - sttConverter isRunning Flag True

            // Send Success Response
            sender.sendSttStartRes(getTId(), callInfo);

            // Schedule
            executors.schedule(() ->{
                // STT Stop
                sttConverter.stop();    // RTP 처리 Stop - sttConverter isRunning Flag False
                // STT 결과 처리
                String result = Optional.ofNullable(sttConverter.getResultTexts()).filter(o -> !o.isEmpty()).map(o -> o.get(o.size() - 1)).orElse("");
                log.debug("{}SttStartReq STT Result : {}", callInfo.getLogHeader(), result);

                // Send SttResultReq
                sender.sendSttResultReq(getTId(), callInfo, result);
            }, sttDur, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.error("{}SttStartReq.sttConverter.Exception ", callInfo.getLogHeader(), e);
        }

    }
}
