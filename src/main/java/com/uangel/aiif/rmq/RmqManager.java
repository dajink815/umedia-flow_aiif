package com.uangel.aiif.rmq;

import com.uangel.aiif.config.AiifConfig;
import com.uangel.aiif.rmq.handler.incoming.RmqConsumer;
import com.uangel.rmq.message.RmqMessage;
import com.uangel.aiif.rmq.module.RmqClient;
import com.uangel.aiif.rmq.module.RmqServer;
import com.uangel.aiif.service.AppInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * @author dajin kim
 */
public class RmqManager {
    static final Logger log = LoggerFactory.getLogger(RmqManager.class);
    private static RmqManager rmqManager = null;
    private final AppInstance instance = AppInstance.getInstance();
    private final AiifConfig config = instance.getConfig();
    private ExecutorService executorRmqService;

    private RmqServer rmqServer;
    private final ConcurrentHashMap<String, RmqClient> rmqClientMap = new ConcurrentHashMap<>();

    private RmqManager() {
        // nothing
    }

    public static RmqManager getInstance() {
        if (rmqManager == null) {
            rmqManager = new RmqManager();
        }
        return rmqManager;
    }

    public void start() {
        startRmqConsumer();
        startRmqClient();
        startRmqServer();
    }

    public void stop() {
        if (rmqServer != null) {
            rmqServer.stop();
        }
        if (!rmqClientMap.isEmpty()) {
            rmqClientMap.forEach((key, client) -> client.closeSender());
        }
        if (executorRmqService != null) {
            executorRmqService.shutdown();
        }
    }

    private void startRmqConsumer() {
        if (executorRmqService != null) return;

        executorRmqService = Executors.newFixedThreadPool(config.getRmqThreadSize());
        BlockingQueue<RmqMessage> rmqMsgQueue = new ArrayBlockingQueue<>(config.getRmqQueueSize());

        instance.setRmqMsgQueue(rmqMsgQueue);

        for (int i = 0; i < config.getRmqThreadSize(); i++) {
            executorRmqService.execute(() -> new Thread(new RmqConsumer(rmqMsgQueue)).start());
        }
    }

    // Server
    private void startRmqServer() {
        // AIIF Server
        if (rmqServer == null) {
            RmqServer rmqAiwfServer = new RmqServer(config.getAiwfHost(), config.getAiwfUser(), config.getAiwfPass(), config.getAiif(), config.getAiwfPort());
            if (rmqAiwfServer.start()) {
                log.debug("RabbitMQ Server Start Success. [{}], [{}], [{}]", config.getAiif(), config.getAiwfHost(), config.getAiwfUser());
                rmqServer = rmqAiwfServer;
            }
        }

        // AIWF Server
/*        if (rmqServer == null) {
            RmqServer rmqAiwfServer = new RmqServer(config.getAiwfHost(), config.getAiwfUser(), config.getAiwfPass(), config.getAiwf(), config.getAiwfPort());
            if (rmqAiwfServer.start()) {
                log.debug("RabbitMQ Server Start Success. [{}], [{}], [{}]", config.getAiwf(), config.getAiwfHost(), config.getAiwfUser());
                rmqServer = rmqAiwfServer;
            }
        }*/
    }

    // Client
    private void startRmqClient() {
        // AIWF
        addClient(config.getAiwf(), config.getAiwfHost(), config.getAiwfUser(), config.getAiwfPass(), config.getAiwfPort());

        // AIM
        addClient(config.getAim(), config.getHost(), config.getUser(), config.getPass(), config.getPort());

        // For Test - AIIF
        addClient(config.getAiif(), config.getHost(), config.getUser(), config.getPass(), config.getPort());
    }

    private void addClient(String target, String host, String user, String pass, int port) {
        if (rmqClientMap.get(target) == null) {
            RmqClient client = new RmqClient(host, user, pass, target, port);
            if (client.start()) {
                log.debug("RabbitMQ Client Start Success. [{}], [{}], [{}]", target, host, user);
                rmqClientMap.put(target, client);
            } else {
                log.debug("RabbitMQ Client Start Fail. [{}], [{}], [{}]", target, host, user);
            }
        }
    }

    public RmqClient getRmqClient(String queueName) {
        return rmqClientMap.get(queueName);
    }

    public RmqServer getRmqServer() {
        return rmqServer;
    }

}
