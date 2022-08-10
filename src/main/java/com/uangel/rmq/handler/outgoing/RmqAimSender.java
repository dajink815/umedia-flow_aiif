package com.uangel.rmq.handler.outgoing;

import com.uangel.config.Config;
import com.uangel.rmq.handler.aiif.RmqAimClient;
import com.uangel.rmq.message.RmqMessage;
import com.uangel.service.AppInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dajin kim
 */
public class RmqAimSender {
    static final Logger log = LoggerFactory.getLogger(RmqAimSender.class);
    private final RmqAimClient client = new RmqAimClient();
    private final RmqOutgoingMessage outgoingMessage;
    private final String msgFrom;

    public RmqAimSender() {
        Config config = AppInstance.getInstance().getConfig();
        // Set Target Queue
        outgoingMessage = new RmqOutgoingMessage(config.getAiif());
        msgFrom = config.getAiif();
    }

    public void sendMediaStart(String tId, int reasonCode, String reason, String callId, int rtpPort) {
        RmqMessage message = client.mediaStartRes(tId, msgFrom, reasonCode, reason, callId, rtpPort);
        outgoingMessage.sendTo(message);
    }

    public void sendMediaPlay(int reasonCode, String reason, String callId, String filePath) {
        RmqMessage message = client.mediaPlayReq(msgFrom, reasonCode, reason, callId, filePath);
        outgoingMessage.sendTo(message);
    }

    public void sendMediaDone(String tId, int reasonCode, String reason, String callId) {
        RmqMessage message = client.mediaDoneRes(tId, msgFrom, reasonCode, reason, callId);
        outgoingMessage.sendTo(message);
    }

    public void sendMediaStop(int reasonCode, String reason, String callId) {
        RmqMessage message = client.mediaStopReq(msgFrom, reasonCode, reason, callId);
        outgoingMessage.sendTo(message);
    }
}
