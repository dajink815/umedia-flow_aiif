package com.uangel.aiif.service;

import com.uangel.aiif.config.AiifConfig;
import com.uangel.aiif.rmq.RmqManager;
import com.uangel.aiif.rmq.handler.outgoing.RmqMsgSender;
import com.uangel.aiif.util.SleepUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dajin kim
 */
public class ServiceManager {
    static final Logger log = LoggerFactory.getLogger(ServiceManager.class);
    private static ServiceManager serviceManager = null;
    private static final AppInstance instance = AppInstance.getInstance();
    private boolean isQuit = false;
    private RmqManager rmqManager = null;

    private ServiceManager() {
        instance.setConfig(new AiifConfig(instance.getConfigPath()));
    }

    public static ServiceManager getInstance() {
        if (serviceManager == null) {
            serviceManager = new ServiceManager();
        }

        return serviceManager;
    }

    public void loop() {
        this.startService();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.error("Process is about to quit (Ctrl+C)");
            this.isQuit = true;
            this.stopService();
        }));

        while (!isQuit) {
            try {
                SleepUtil.trySleep(1000);
            } catch (Exception e) {
                log.error("ServiceManager.loop.Exception ", e);
            }
        }

        log.error("Process End");
    }

    private void startService() {
        log.info("Start Service...");

        rmqManager = RmqManager.getInstance();
        rmqManager.start();

        RmqMsgSender sender = RmqMsgSender.getInstance();
        sender.sendIHbReq(0, "Success", 1);
    }

    private void stopService() {
        log.info("Stop Service...");

        if (rmqManager != null)
            rmqManager.stop();
    }


}
