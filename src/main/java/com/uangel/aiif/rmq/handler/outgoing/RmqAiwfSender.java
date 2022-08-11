package com.uangel.aiif.rmq.handler.outgoing;

import com.uangel.aiif.config.AiifConfig;
import com.uangel.rmq.handler.aiif.RmqAiwfClient;
import com.uangel.rmq.message.IHbReq;
import com.uangel.rmq.message.RmqHeader;
import com.uangel.rmq.message.RmqMessage;
import com.uangel.aiif.rmq.types.RmqMsgType;
import com.uangel.aiif.rmq.util.RmqBuilder;
import com.uangel.aiif.service.AppInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dajin kim
 */
public class RmqAiwfSender {
    static final Logger log = LoggerFactory.getLogger(RmqAiwfSender.class);
    private final RmqAiwfClient client = new RmqAiwfClient();
    private final RmqOutgoingMessage outgoingMessage;
    private final String msgFrom;

    public RmqAiwfSender() {
        AiifConfig aiifConfig = AppInstance.getInstance().getConfig();
        // Set Target Queue
        outgoingMessage = new RmqOutgoingMessage(aiifConfig.getAiwf());
        msgFrom = aiifConfig.getAiif();
    }

    public void sendIHb(int reasonCode, String reason, int status) {
/*        RmqMessage message = client.iHbReq(msgFrom, reasonCode, reason, status);
        outgoingMessage.sendTo(message);*/

        RmqHeader.Builder headerBuilder = RmqBuilder.getDefaultHeader(RmqMsgType.I_HB_REQ);
        if (RmqMsgType.isRmqFail(reasonCode)) {
            headerBuilder.setReasonCode(reasonCode);
            headerBuilder.setReason(reason);
        }

        RmqMessage message = RmqMessage.newBuilder()
                .setHeader(headerBuilder.build())
                .setIHbReq(IHbReq.newBuilder()
                        .setStatus(status))
                .build();

        outgoingMessage.sendTo(message);
    }

    public void sendCreateSession(String tId, int reasonCode, String reason, String callId) {
        RmqMessage message = client.createSessionRes(tId, msgFrom, reasonCode, reason, callId);
        outgoingMessage.sendTo(message);
    }

    public void sendDelSession(String tId, int reasonCode, String reason, String callId) {
        RmqMessage message = client.delSessionRes(tId, msgFrom, reasonCode, reason, callId);
        outgoingMessage.sendTo(message);
    }

    public void sendTtsStart(String tId, int reasonCode, String reason, String callId) {
        RmqMessage message = client.ttsStartRes(tId, msgFrom, reasonCode, reason, callId);
        outgoingMessage.sendTo(message);
    }

    public void sendTtsResult(int reasonCode, String reason, String callId) {
        RmqMessage message = client.ttsResultReq(msgFrom, reasonCode, reason, callId);
        outgoingMessage.sendTo(message);
    }

    public void sendSttStart(String tId, int reasonCode, String reason, String callId) {
        RmqMessage message = client.sttStartRes(tId, msgFrom, reasonCode, reason, callId);
        outgoingMessage.sendTo(message);
    }

    public void sendSttResult(int reasonCode, String reason, String callId, String resultText) {
        RmqMessage message = client.sttResultReq(msgFrom, reasonCode, reason, callId, resultText);
        outgoingMessage.sendTo(message);
    }


}
