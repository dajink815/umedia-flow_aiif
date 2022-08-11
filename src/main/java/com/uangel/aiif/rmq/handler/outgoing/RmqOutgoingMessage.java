package com.uangel.aiif.rmq.handler.outgoing;

import com.google.protobuf.util.JsonFormat;
import com.uangel.aiif.config.AiifConfig;
import com.uangel.aiif.rmq.RmqManager;
import com.uangel.rmq.message.RmqHeader;
import com.uangel.rmq.message.RmqMessage;
import com.uangel.aiif.rmq.module.RmqClient;
import com.uangel.aiif.service.AppInstance;
import com.uangel.aiif.util.StringUtil;
import com.uangel.aiif.util.Suppress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dajin kim
 */
public class RmqOutgoingMessage {
    static final Logger log = LoggerFactory.getLogger(RmqOutgoingMessage.class);
    private static final Suppress suppr = new Suppress(1000L * 30);
    private RmqMessage rmqMessage;
    private String target;

    public RmqOutgoingMessage(RmqMessage rmqMessage) {
        this.rmqMessage = rmqMessage;
    }

    public RmqOutgoingMessage(String target) {
        this.target = target;
    }

    public boolean sendTo(String target) {
        return sendTo(target, this.rmqMessage);
    }

    public boolean sendTo(RmqMessage rmqMessage) {
        return sendTo(this.target, rmqMessage);
    }

    public boolean sendTo(String target, RmqMessage rmqMessage) {
        boolean result = false;

        AiifConfig aiifConfig = AppInstance.getInstance().getConfig();
        if (StringUtil.isNull(target)) {
            target = aiifConfig.getAiwf();
        }

        try {
            String json = JsonFormat.printer().includingDefaultValueFields().print(rmqMessage);

            RmqHeader header = rmqMessage.getHeader();
            String msgType = header.getType();

            if (rmqMessage.getBodyCase().getNumber() == RmqMessage.IHBREQ_FIELD_NUMBER) {
                //if (suppr.touch(msgType + header.getMsgFrom())) {
                    log.info("[RMQ MESSAGE] send [{}] [{}] --> [{}]", msgType, header.getReasonCode(), target);
                    log.debug("[RMQ MESSAGE] Json --> {}", json);
                //}
            } else {
                log.info("[RMQ MESSAGE] send [{}] [{}] --> [{}]", msgType, header.getReasonCode(), target);
                log.debug("[RMQ MESSAGE] Json --> {}", json);
            }

            // Check Body Type
            String bodyCase = rmqMessage.getBodyCase().toString();
            String typeCheck = StringUtil.removeUnderBar(msgType);
            if (!bodyCase.equalsIgnoreCase(typeCheck)) {
                log.warn("RmqOutgoingMessage.sendTo Check Body type [{}]", bodyCase);
            } else {
                log.debug("RmqOutgoingMessage.sendTo Body type [{}]", bodyCase);
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
}
