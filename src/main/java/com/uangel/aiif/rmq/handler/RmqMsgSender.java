package com.uangel.aiif.rmq.handler;

import com.uangel.aiif.rmq.common.RmqMsgType;
import com.uangel.aiif.rmq.handler.aim.outgoing.RmqMediaDoneRes;
import com.uangel.aiif.rmq.handler.aim.outgoing.RmqMediaPlayReq;
import com.uangel.aiif.rmq.handler.aim.outgoing.RmqMediaStartRes;
import com.uangel.aiif.rmq.handler.aim.outgoing.RmqMediaStopReq;
import com.uangel.aiif.rmq.handler.aiwf.outgoing.*;
import com.uangel.aiif.session.model.SessionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dajin kim
 */
public class RmqMsgSender {
    static final Logger log = LoggerFactory.getLogger(RmqMsgSender.class);
    private static RmqMsgSender rmqMsgSender = null;

    public static RmqMsgSender getInstance() {
        if (rmqMsgSender == null)
            rmqMsgSender = new RmqMsgSender();

        return rmqMsgSender;
    }

    // AIWF
    public void sendIHbReq(int status) {
        RmqIHbReq req = new RmqIHbReq();
        req.send(status, RmqMsgType.I_HB_REQ);
    }

    public void sendCreateSessionRes(String tId, SessionInfo sessionInfo) {
        RmqCreateSessionRes res = new RmqCreateSessionRes();
        res.send(tId, sessionInfo, RmqMsgType.CREATE_SESSION_RES);
    }
    public void sendCreateSessionRes(String tId, int reasonCode, String reason, String callId) {
        RmqCreateSessionRes res = new RmqCreateSessionRes();
        res.send(tId, reasonCode, reason, callId, RmqMsgType.CREATE_SESSION_RES);
    }

    public void sendDelSessionRes(String tId, SessionInfo sessionInfo) {
        RmqDelSessionRes res = new RmqDelSessionRes();
        res.send(tId, sessionInfo, RmqMsgType.DEL_SESSION_RES);
    }
    public void sendDelSessionRes(String tId, int reasonCode, String reason, String callId) {
        RmqDelSessionRes res = new RmqDelSessionRes();
        res.send(tId, reasonCode, reason, callId, RmqMsgType.DEL_SESSION_RES);
    }

    public void sendTtsStartRes(String tId, SessionInfo sessionInfo) {
        RmqTtsStartRes res = new RmqTtsStartRes();
        res.send(tId, sessionInfo, RmqMsgType.TTS_START_RES);
    }
    public void sendTtsStartRes(String tId, int reasonCode, String reason, SessionInfo sessionInfo) {
        RmqTtsStartRes res = new RmqTtsStartRes();
        res.send(tId, reasonCode, reason, sessionInfo, RmqMsgType.TTS_START_RES);
    }

    public void sendTtsResultReq(SessionInfo sessionInfo) {
        RmqTtsResultReq req = new RmqTtsResultReq();
        req.send(sessionInfo, RmqMsgType.TTS_RESULT_REQ);
    }
    public void sendTtsResultReq(int reasonCode, String reason, SessionInfo sessionInfo) {
        RmqTtsResultReq req = new RmqTtsResultReq();
        req.send(reasonCode, reason, sessionInfo, RmqMsgType.TTS_RESULT_REQ);
    }

    public void sendSttStartRes(String tId, SessionInfo sessionInfo) {
        RmqSttStartRes res = new RmqSttStartRes();
        res.send(tId, sessionInfo, RmqMsgType.STT_START_RES);
    }
    public void sendSttStartRes(String tId, int reasonCode, String reason, SessionInfo sessionInfo) {
        RmqSttStartRes res = new RmqSttStartRes();
        res.send(tId, reasonCode, reason, sessionInfo, RmqMsgType.STT_START_RES);
    }

    public void sendSttResultReq(SessionInfo sessionInfo) {
        RmqSttResultReq req = new RmqSttResultReq();
        req.send(sessionInfo, RmqMsgType.STT_RESULT_REQ);

    }
    public void sendSttResultReq(int reasonCode, String reason, SessionInfo sessionInfo) {
        RmqSttResultReq req = new RmqSttResultReq();
        req.send(reasonCode, reason, sessionInfo, RmqMsgType.STT_RESULT_REQ);

    }

    // AIM
    public void sendMediaStartRes(String tId, SessionInfo sessionInfo) {
        RmqMediaStartRes res = new RmqMediaStartRes();
        res.send(tId, sessionInfo, RmqMsgType.MEDIA_START_RES);
    }
    public void sendMediaStartRes(String tId, int reasonCode, String reason, SessionInfo sessionInfo) {
        RmqMediaStartRes res = new RmqMediaStartRes();
        res.send(tId, reasonCode, reason, sessionInfo, RmqMsgType.MEDIA_START_RES);
    }

    public void sendMediaPlayReq(SessionInfo sessionInfo) {
        RmqMediaPlayReq req = new RmqMediaPlayReq();
        req.send(sessionInfo, RmqMsgType.MEDIA_PLAY_REQ);
    }
    public void sendMediaPlayReq(int reasonCode, String reason, SessionInfo sessionInfo) {
        RmqMediaPlayReq req = new RmqMediaPlayReq();
        req.send(reasonCode, reason, sessionInfo, RmqMsgType.MEDIA_PLAY_REQ);
    }

    public void sendMediaDoneRes(String tId, SessionInfo sessionInfo) {
        RmqMediaDoneRes res = new RmqMediaDoneRes();
        res.send(tId, sessionInfo, RmqMsgType.MEDIA_DONE_RES);
    }
    public void sendMediaDoneRes(String tId, int reasonCode, String reason, SessionInfo sessionInfo) {
        RmqMediaDoneRes res = new RmqMediaDoneRes();
        res.send(tId, reasonCode, reason, sessionInfo, RmqMsgType.MEDIA_DONE_RES);
    }

    public void sendMediaStopReq(SessionInfo sessionInfo) {
        RmqMediaStopReq req = new RmqMediaStopReq();
        req.send(sessionInfo, RmqMsgType.MEDIA_STOP_REQ);

    }
    public void sendMediaStopReq(int reasonCode, String reason, SessionInfo sessionInfo) {
        RmqMediaStopReq req = new RmqMediaStopReq();
        req.send(reasonCode, reason, sessionInfo, RmqMsgType.MEDIA_STOP_REQ);
    }

}
