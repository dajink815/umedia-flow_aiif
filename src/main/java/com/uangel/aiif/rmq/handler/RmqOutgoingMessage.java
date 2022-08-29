package com.uangel.aiif.rmq.handler;

import com.google.protobuf.util.JsonFormat;
import com.uangel.aiif.config.AiifConfig;
import com.uangel.aiif.rmq.RmqManager;
import com.uangel.aiif.rmq.module.RmqClient;
import com.uangel.aiif.service.AppInstance;
import com.uangel.aiif.util.StringUtil;
import com.uangel.aiif.util.Suppress;
import com.uangel.protobuf.Header;
import com.uangel.protobuf.Message;
import com.uangel.protobuf.MessageBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dajin kim
 */
public class RmqOutgoingMessage {
    static final Logger log = LoggerFactory.getLogger(RmqOutgoingMessage.class);
    private static final Suppress suppr = new Suppress(1000L * 30);
    private static final AiifConfig config = AppInstance.getInstance().getConfig();

    public RmqOutgoingMessage() {
        // nothing
    }

    public boolean sendToAim(MessageBuilder builder) {
        return this.sendToAim(builder.build());
    }
    public boolean sendToAim(Message rmqMessage) {
        return this.sendTo(config.getAim(), rmqMessage);
    }

    public boolean sendToAiwf(MessageBuilder builder) {
        return this.sendToAiwf(builder.build());
    }
    public boolean sendToAiwf(Message rmqMessage) {
        return this.sendTo(config.getAiwf(), rmqMessage);
    }

    private boolean sendTo(String target, Message rmqMessage) {
        boolean result = false;

        if (StringUtil.isNull(target)) {
            target = config.getAiwf();
        }

        try {
            String json = JsonFormat.printer().includingDefaultValueFields().print(rmqMessage);

            Header header = rmqMessage.getHeader();
            String msgType = header.getType();

            if (rmqMessage.getBodyCase().getNumber() == Message.IHBREQ_FIELD_NUMBER) {
                if (suppr.touch(msgType + header.getMsgFrom())) {
                    printMsg(rmqMessage, json, target);
                }
            } else {
                printMsg(rmqMessage, json, target);
            }


            RmqClient client = RmqManager.getInstance().getRmqClient(target);
            if (client != null) {
                result = client.send(rmqMessage.toByteArray());
                // RMQ 메시지 전송 실패
                if (!result) {
                    log.warn("() () () send failed [{}] -> [{}]", msgType, target);
                }
            } else {
                log.warn("() () () RmqClient is Null [{}]", target);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private void printMsg(Message rmqMessage, String json, String target) {
        Header header = rmqMessage.getHeader();
        String msgType = header.getType();

        log.info("[RMQ MESSAGE] send [{}] [{}] --> [{}]", msgType, header.getReasonCode(), target);
        log.debug("[RMQ MESSAGE] Json --> {}", json);

        // Check Body Type
        String bodyCase = rmqMessage.getBodyCase().toString();
        String typeCheck = StringUtil.removeUnderBar(msgType);
        if (!bodyCase.equalsIgnoreCase(typeCheck)) {
            log.warn("RmqOutgoingMessage.sendTo Check Body type [{}]", bodyCase);
        } else {
            log.debug("RmqOutgoingMessage.sendTo Body type [{}]", bodyCase);
        }
    }
}
