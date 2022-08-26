package com.uangel.aiif.rmq.handler;

import com.uangel.aiif.rmq.common.RmqMsgType;
import com.uangel.aiif.rmq.handler.aim.outgoing.RmqMediaDoneRes;
import com.uangel.aiif.rmq.handler.aim.outgoing.RmqMediaPlayReq;
import com.uangel.aiif.rmq.handler.aim.outgoing.RmqMediaStartRes;
import com.uangel.aiif.rmq.handler.aim.outgoing.RmqMediaStopReq;
import com.uangel.aiif.rmq.handler.aiwf.outgoing.*;
import com.uangel.aiif.session.model.CallInfo;
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

    public void sendCreateSessionRes(String tId, CallInfo callInfo) {
        RmqCreateSessionRes res = new RmqCreateSessionRes();
        res.send(tId, callInfo, RmqMsgType.CREATE_SESSION_RES);
    }
    public void sendCreateSessionRes(String tId, int reasonCode, String reason, String callId) {
        RmqCreateSessionRes res = new RmqCreateSessionRes();
        res.send(tId, reasonCode, reason, callId, RmqMsgType.CREATE_SESSION_RES);
    }

    public void sendDelSessionRes(String tId, String callId) {
        RmqDelSessionRes res = new RmqDelSessionRes();
        res.send(tId, callId, RmqMsgType.DEL_SESSION_RES);
    }
    public void sendDelSessionRes(String tId, int reasonCode, String reason, String callId) {
        RmqDelSessionRes res = new RmqDelSessionRes();
        res.send(tId, reasonCode, reason, callId, RmqMsgType.DEL_SESSION_RES);
    }

    public void sendTtsStartRes(String tId, CallInfo callInfo) {
        RmqTtsStartRes res = new RmqTtsStartRes();
        res.send(tId, callInfo, RmqMsgType.TTS_START_RES);
    }
    public void sendTtsStartRes(String tId, int reasonCode, String reason, CallInfo callInfo) {
        RmqTtsStartRes res = new RmqTtsStartRes();
        res.send(tId, reasonCode, reason, callInfo, RmqMsgType.TTS_START_RES);
    }
    public void sendTtsStartRes(String tId, int reasonCode, String reason, String callId) {
        RmqTtsStartRes res = new RmqTtsStartRes();
        res.send(tId, reasonCode, reason, callId, RmqMsgType.TTS_START_RES);
    }

    public void sendTtsResultReq(CallInfo callInfo) {
        RmqTtsResultReq req = new RmqTtsResultReq();
        req.send(callInfo, RmqMsgType.TTS_RESULT_REQ);
    }
    public void sendTtsResultReq(int reasonCode, String reason, CallInfo callInfo) {
        RmqTtsResultReq req = new RmqTtsResultReq();
        req.send(reasonCode, reason, callInfo, RmqMsgType.TTS_RESULT_REQ);
    }

    public void sendSttStartRes(String tId, CallInfo callInfo) {
        RmqSttStartRes res = new RmqSttStartRes();
        res.send(tId, callInfo, RmqMsgType.STT_START_RES);
    }
    public void sendSttStartRes(String tId, int reasonCode, String reason, String callId) {
        RmqSttStartRes res = new RmqSttStartRes();
        res.send(tId, reasonCode, reason, callId, RmqMsgType.STT_START_RES);
    }

    public void sendSttResultReq(String tId, CallInfo callInfo, String resultTxt) {
        RmqSttResultReq req = new RmqSttResultReq();
        req.send(tId, callInfo, resultTxt, RmqMsgType.STT_RESULT_REQ);

    }
    public void sendSttResultReq(String tId, int reasonCode, String reason, CallInfo callInfo, String resultTxt) {
        RmqSttResultReq req = new RmqSttResultReq();
        req.send(tId, reasonCode, reason, callInfo, resultTxt,  RmqMsgType.STT_RESULT_REQ);

    }

    // AIM
    public void sendMediaStartRes(String tId, CallInfo callInfo) {
        RmqMediaStartRes res = new RmqMediaStartRes();
        res.send(tId, callInfo, RmqMsgType.MEDIA_START_RES);
    }
    public void sendMediaStartRes(String tId, int reasonCode, String reason, CallInfo callInfo) {
        RmqMediaStartRes res = new RmqMediaStartRes();
        res.send(tId, reasonCode, reason, callInfo, RmqMsgType.MEDIA_START_RES);
    }
    public void sendMediaStartRes(String tId, int reasonCode, String reason, String callId) {
        RmqMediaStartRes res = new RmqMediaStartRes();
        res.send(tId, reasonCode, reason, callId, RmqMsgType.MEDIA_START_RES);
    }

    public void sendMediaPlayReq(CallInfo callInfo, String filePath) {
        RmqMediaPlayReq req = new RmqMediaPlayReq();
        req.send(callInfo, filePath, RmqMsgType.MEDIA_PLAY_REQ);
    }
    public void sendMediaPlayReq(int reasonCode, String reason, CallInfo callInfo, String filePath) {
        RmqMediaPlayReq req = new RmqMediaPlayReq();
        req.send(reasonCode, reason, callInfo, filePath, RmqMsgType.MEDIA_PLAY_REQ);
    }

    public void sendMediaDoneRes(String tId, CallInfo callInfo) {
        RmqMediaDoneRes res = new RmqMediaDoneRes();
        res.send(tId, callInfo, RmqMsgType.MEDIA_DONE_RES);
    }
    public void sendMediaDoneRes(String tId, int reasonCode, String reason, CallInfo callInfo) {
        RmqMediaDoneRes res = new RmqMediaDoneRes();
        res.send(tId, reasonCode, reason, callInfo, RmqMsgType.MEDIA_DONE_RES);
    }
    public void sendMediaDoneRes(String tId, int reasonCode, String reason, String callId) {
        RmqMediaDoneRes res = new RmqMediaDoneRes();
        res.send(tId, reasonCode, reason, callId, RmqMsgType.MEDIA_DONE_RES);
    }

    public void sendMediaStopReq(CallInfo callInfo) {
        RmqMediaStopReq req = new RmqMediaStopReq();
        req.send(callInfo, RmqMsgType.MEDIA_STOP_REQ);

    }
    public void sendMediaStopReq(int reasonCode, String reason, CallInfo callInfo) {
        RmqMediaStopReq req = new RmqMediaStopReq();
        req.send(reasonCode, reason, callInfo, RmqMsgType.MEDIA_STOP_REQ);
    }

}
