package com.uangel.aiif.rmq.handler;

import com.uangel.aiif.config.AiifConfig;
import com.uangel.aiif.rmq.handler.aim.RmqAimConsumer;
import com.uangel.aiif.rmq.handler.aiwf.RmqAiwfConsumer;
import com.uangel.protobuf.Message;
import com.uangel.aiif.service.AppInstance;
import com.uangel.aiif.util.SleepUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author dajin kim
 */
public class RmqConsumer implements Runnable {
    static final Logger log = LoggerFactory.getLogger(RmqConsumer.class);
    private final AiifConfig aiifConfig = AppInstance.getInstance().getConfig();
    private final BlockingQueue<Message> rmqMsgQueue;
    private boolean isQuit = false;

    public RmqConsumer(BlockingQueue<Message> queue) {
        this.rmqMsgQueue = queue;
    }

    @Override
    public void run() {
        queueProcessing();
    }

    private void queueProcessing() {
        while (!isQuit) {
            try {
                Message rmqMsg = rmqMsgQueue.poll(10, TimeUnit.MILLISECONDS);

                if (rmqMsg == null) {
                    SleepUtil.trySleep(10);
                    continue;
                }
                messageProcessing(rmqMsg);

            } catch (InterruptedException e) {
                log.error("RmqConsumer.queueProcessing", e);
                isQuit = true;
                Thread.currentThread().interrupt();
            }
        }
    }

    private void messageProcessing(Message msg) {
        String msgFrom = msg.getHeader().getMsgFrom();
        if (aiifConfig.getAiwf().contains(msgFrom)) {
            RmqAiwfConsumer consumer = new RmqAiwfConsumer();
            consumer.aiwfMessageProcessing(msg);
        } else if (aiifConfig.getAim().contains(msgFrom)) {
            RmqAimConsumer consumer = new RmqAimConsumer();
            consumer.aimMessageProcessing(msg);
        } else {
            log.warn("RmqConsumer.messageProcessing - Check MsgFrom [{}:{} ({})]", msg.getHeader().getType(), msgFrom, msg.getHeader().getTId());
        }
    }
}

