package com.uangel.aiif.rmq.handler;

import com.uangel.aiif.service.AppInstance;
import com.uangel.aiif.session.model.CallInfo;
import com.uangel.protobuf.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dajin kim
 */
public class RmqMsgSender extends RmqOutgoingMessage {
    static final Logger log = LoggerFactory.getLogger(RmqMsgSender.class);
    private static RmqMsgSender sender = null;

    public static RmqMsgSender getInstance() {
        if (sender == null)
            sender = new RmqMsgSender();
        return sender;
    }

    // AIWF
    public void sendIHbReq(int status) {
        sendToAiwf(new MessageBuilder()
                .setBody(IHbReq.newBuilder()
                        .setStatus(status).build()));
    }

    public void sendCreateSessionRes(String tId, CallInfo callInfo) {
        sendToAiwf(new MessageBuilder()
                .setBody(CreateSessionRes.newBuilder()
                        .setCallId(callInfo.getCallId()).build())
                .settId(tId));
    }
    public void sendCreateSessionRes(String tId, int reasonCode, String reason, String callId) {
        sendToAiwf(new MessageBuilder()
                .setBody(CreateSessionRes.newBuilder()
                        .setCallId(callId).build())
                .settId(tId)
                .setReasonCode(reasonCode)
                .setReason(reason));
    }

    public void sendDelSessionRes(String tId, String callId) {
        sendToAiwf(new MessageBuilder()
                .setBody(DelSessionRes.newBuilder()
                        .setCallId(callId).build())
                .settId(tId));
    }
    public void sendDelSessionRes(String tId, int reasonCode, String reason, String callId) {
        sendToAiwf(new MessageBuilder()
                .setBody(DelSessionRes.newBuilder()
                        .setCallId(callId).build())
                .settId(tId)
                .setReasonCode(reasonCode)
                .setReason(reason));
    }

    public void sendTtsStartRes(String tId, CallInfo callInfo) {
        sendToAiwf(new MessageBuilder()
                .setBody(TtsStartRes.newBuilder()
                        .setCallId(callInfo.getCallId())
                        .setDialogId(callInfo.getTtsDialogId()).build())
                .settId(tId));
    }
    public void sendTtsStartRes(String tId, int reasonCode, String reason, String callId, String dialogId) {
        sendToAiwf(new MessageBuilder()
                .setBody(TtsStartRes.newBuilder()
                        .setCallId(callId)
                        .setDialogId(dialogId).build())
                .settId(tId)
                .setReasonCode(reasonCode)
                .setReason(reason));
    }

    public void sendTtsResultReq(CallInfo callInfo) {
        sendToAiwf(new MessageBuilder()
                .setBody(TtsResultReq.newBuilder()
                        .setCallId(callInfo.getCallId())
                        .setDialogId(callInfo.getTtsDialogId()).build()));
    }
    public void sendTtsResultReq(int reasonCode, String reason, CallInfo callInfo) {
        sendToAiwf(new MessageBuilder()
                .setBody(TtsResultReq.newBuilder()
                        .setCallId(callInfo.getCallId())
                        .setDialogId(callInfo.getTtsDialogId()).build())
                .setReasonCode(reasonCode)
                .setReason(reason));
    }

    public void sendSttStartRes(String tId, CallInfo callInfo) {
        sendToAiwf(new MessageBuilder()
                .setBody(SttStartRes.newBuilder()
                        .setCallId(callInfo.getCallId())
                        .setDialogId(callInfo.getSttDialogId()).build())
                .settId(tId));
    }
    public void sendSttStartRes(String tId, int reasonCode, String reason, String callId, String dialogId) {
        sendToAiwf(new MessageBuilder()
                .setBody(SttStartRes.newBuilder()
                        .setCallId(callId)
                        .setDialogId(dialogId).build())
                .settId(tId)
                .setReasonCode(reasonCode)
                .setReason(reason));
    }

    public void sendSttResultReq(String tId, CallInfo callInfo, String resultTxt) {
        sendToAiwf(new MessageBuilder()
                .setBody(SttResultReq.newBuilder()
                        .setCallId(callInfo.getCallId())
                        .setDialogId(callInfo.getSttDialogId())
                        .setResultText(resultTxt).build())
                .settId(tId));

    }
    public void sendSttResultReq(String tId, int reasonCode, String reason, CallInfo callInfo, String resultTxt) {
        sendToAiwf(new MessageBuilder()
                .setBody(SttResultReq.newBuilder()
                        .setCallId(callInfo.getCallId())
                        .setDialogId(callInfo.getSttDialogId())
                        .setResultText(resultTxt).build())
                .settId(tId)
                .setReasonCode(reasonCode)
                .setReason(reason));
    }

    // AIM
    public void sendMediaStartRes(String tId, CallInfo callInfo) {
        sendToAim(new MessageBuilder()
                .setBody(MediaStartRes.newBuilder()
                        .setCallId(callInfo.getCallId())
                        .setRtpIp(AppInstance.getInstance().getConfig().getServerIp())
                        .setRtpPort(callInfo.getRtpPort()).build())
                .settId(tId));
    }
    public void sendMediaStartRes(String tId, int reasonCode, String reason, String callId) {
        sendToAim(new MessageBuilder()
                .setBody(MediaStartRes.newBuilder()
                        .setCallId(callId).build())
                .settId(tId)
                .setReasonCode(reasonCode)
                .setReason(reason));
    }

    public void sendMediaPlayReq(CallInfo callInfo, String filePath, String dialogId) {
        // Clearing 체크
        if (callInfo.isClearing()) return;

        sendToAim(new MessageBuilder()
                .setBody(MediaPlayReq.newBuilder()
                        .setCallId(callInfo.getCallId())
                        .setDialogId(dialogId)
                        .setFilePath(filePath).build()));
    }

    public void sendMediaDoneRes(String tId, CallInfo callInfo, String dialogId) {
        sendToAim(new MessageBuilder()
                .setBody(MediaDoneRes.newBuilder()
                        .setCallId(callInfo.getCallId())
                        .setDialogId(dialogId).build())
                .settId(tId));
    }
    public void sendMediaDoneRes(String tId, int reasonCode, String reason, String callId, String dialogId) {
        sendToAim(new MessageBuilder()
                .setBody(MediaDoneRes.newBuilder()
                        .setCallId(callId)
                        .setDialogId(dialogId).build())
                .settId(tId)
                .setReasonCode(reasonCode)
                .setReason(reason));
    }

    public void sendMediaStopReq(CallInfo callInfo) {
        sendToAim(new MessageBuilder()
                .setBody(MediaStopReq.newBuilder()
                        .setCallId(callInfo.getCallId()).build()));

    }
    public void sendMediaStopReq(int reasonCode, String reason, CallInfo callInfo) {
        sendToAim(new MessageBuilder()
                .setBody(MediaStopReq.newBuilder()
                        .setCallId(callInfo.getCallId()).build())
                .setReasonCode(reasonCode)
                .setReason(reason));
    }

}
