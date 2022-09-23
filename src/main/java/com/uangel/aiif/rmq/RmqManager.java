package com.uangel.aiif.rmq;

import com.uangel.aiif.config.AiifConfig;
import com.uangel.aiif.rmq.handler.RmqConsumer;
import com.uangel.aiif.util.StringUtil;
import com.uangel.protobuf.Message;
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
        BlockingQueue<Message> rmqMsgQueue = new ArrayBlockingQueue<>(config.getRmqQueueSize());

        instance.setRmqMsgQueue(rmqMsgQueue);

        for (int i = 0; i < config.getRmqThreadSize(); i++) {
            executorRmqService.execute(new RmqConsumer(rmqMsgQueue));
        }
    }

    // Server
    private void startRmqServer() {
        // AIIF Server
        if (rmqServer == null) {
            String target = config.getAiif();
            String host = config.getAiwfHost();
            String user = config.getAiwfUser();
            String pass = config.getAiwfPass();
            int port = config.getAiwfPort();

            RmqServer rmqAiwfServer = new RmqServer(host, user, pass, target, port);
            boolean result = rmqAiwfServer.start();
            if (result) rmqServer = rmqAiwfServer;
            instance.setRmqConnect(result);
            log.info("RabbitMQ Server Start {}. [{}], [{}], [{}]", StringUtil.getSucFail(result), target, host, user);
        }
    }

    // Client
    private void startRmqClient() {
        // AIWF
        boolean aiwfResult = addClient(config.getAiwf(), config.getAiwfHost(), config.getAiwfUser(), config.getAiwfPass(), config.getAiwfPort());
        instance.setRmqConnect(aiwfResult);

        // AIM
        addClient(config.getAim(), config.getHost(), config.getUser(), config.getPass(), config.getPort());
        // aim rmq connect result
    }

    private boolean addClient(String target, String host, String user, String pass, int port) {
        boolean result = false;
        if (rmqClientMap.get(target) == null) {
            RmqClient client = new RmqClient(host, user, pass, target, port);
            result = client.start();
            if (result) rmqClientMap.put(target, client);
            log.info("RabbitMQ Client Start {}. [{}], [{}], [{}]", StringUtil.getSucFail(result), target, host, user);
        }
        return result;
    }

    public RmqClient getRmqClient(String queueName) {
        return rmqClientMap.get(queueName);
    }

    public RmqServer getRmqServer() {
        return rmqServer;
    }

}
