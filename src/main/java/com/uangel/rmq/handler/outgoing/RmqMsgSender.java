package com.uangel.rmq.handler.outgoing;

import com.uangel.rmq.message.RmqMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dajin kim
 */
public class RmqMsgSender {
    static final Logger log = LoggerFactory.getLogger(RmqMsgSender.class);
    private static RmqMsgSender rmqMsgSender = null;
    private final RmqAiwfSender aiwfSender = new RmqAiwfSender();
    private final RmqAimSender aimSender = new RmqAimSender();

    public static RmqMsgSender getInstance() {
        if (rmqMsgSender == null)
            rmqMsgSender = new RmqMsgSender();

        return rmqMsgSender;
    }

    // AIWF
    public void sendIHbReq(int reasonCode, String reason, int status) {
        aiwfSender.sendIHb(reasonCode, reason, status);
    }

    public void sendCreateSessionRes(String tId, int reasonCode, String reason, String callId) {
        aiwfSender.sendCreateSession(tId, reasonCode, reason, callId);
    }

    public void sendDelSessionRes(String tId, int reasonCode, String reason, String callId) {
        aiwfSender.sendDelSession(tId, reasonCode, reason, callId);
    }

    public void sendTtsStartRes(String tId, int reasonCode, String reason, String callId) {
        aiwfSender.sendTtsStart(tId, reasonCode, reason, callId);

    }

    public void sendTtsResultReq(int reasonCode, String reason, String callId) {
        aiwfSender.sendTtsResult(reasonCode, reason, callId);

    }

    public void sendSttStartRes(String tId, int reasonCode, String reason, String callId) {
        aiwfSender.sendSttStart(tId, reasonCode, reason, callId);

    }

    public void sendSttResultReq(int reasonCode, String reason, String callId, String resultText) {
        aiwfSender.sendSttResult(reasonCode, reason, callId, resultText);
    }


    // AIM
    public void sendMediaStartRes(String tId, int reasonCode, String reason, String callId, int rtpPort) {
        aimSender.sendMediaStart(tId, reasonCode, reason, callId, rtpPort);
    }

    public void sendMediaPlayReq(int reasonCode, String reason, String callId, String filePath) {
        aimSender.sendMediaPlay(reasonCode, reason, callId, filePath);
    }

    public void sendMediaDoneRes(String tId, int reasonCode, String reason, String callId) {
        aimSender.sendMediaDone(tId, reasonCode, reason, callId);
    }

    public void sendMediaStopReq(int reasonCode, String reason, String callId) {
        aimSender.sendMediaStop(reasonCode, reason, callId);
    }

}
