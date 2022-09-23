package com.uangel.aiif.service;

import com.uangel.aiif.config.AiifConfig;
import com.uangel.aiif.rmq.RmqManager;
import com.uangel.aiif.rtpcore.service.NettyChannelManager;
import com.uangel.aiif.service.schedule.IntervalTaskManager;
import com.uangel.aiif.util.SleepUtil;
import com.uangel.protobuf.MessageBuilder;
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
    private IntervalTaskManager intervalTaskManager = null;
    private NettyChannelManager nettyChannelManager = null;

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

        // Default msgFrom
        MessageBuilder.setDefaultMsgFrom(instance.getConfig().getAiif());

        rmqManager = RmqManager.getInstance();
        rmqManager.start();

        this.intervalTaskManager = IntervalTaskManager.getInstance();
        try {
            this.intervalTaskManager.init();
            this.intervalTaskManager.start();
        } catch (Exception e) {
            log.error("IntervalTaskManager.start.Exception", e);
        }

        nettyChannelManager = NettyChannelManager.getInstance();
        nettyChannelManager.openRtpServer();
    }

    private void stopService() {
        log.info("Stop Service...");

        if (rmqManager != null)
            rmqManager.stop();

        if (intervalTaskManager != null)
            intervalTaskManager.stop();

        if (nettyChannelManager != null)
            nettyChannelManager.closeRtpServer();
    }


}
