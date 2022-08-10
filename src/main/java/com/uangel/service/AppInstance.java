package com.uangel.service;

import com.uangel.config.Config;
import com.uangel.rmq.message.RmqMessage;

import java.util.concurrent.BlockingQueue;

/**
 * @author dajin kim
 */
public class AppInstance {
    private static AppInstance instance = null;

    private String configPath = null;
    private Config config = null;

    // RMQ
    private BlockingQueue<RmqMessage> rmqMsgQueue;

    private AppInstance() {
        // nothing
    }

    public static AppInstance getInstance() {
        if (instance == null) {
            instance = new AppInstance();
        }
        return instance;
    }

    public String getConfigPath() {
        return configPath;
    }
    public void setConfigPath(String configPath) {
        this.configPath = configPath;
    }

    public Config getConfig() {
        return config;
    }
    public void setConfig(Config config) {
        this.config = config;
    }

    public BlockingQueue<RmqMessage> getRmqMsgQueue() {
        return rmqMsgQueue;
    }
    public void setRmqMsgQueue(BlockingQueue<RmqMessage> rmqMsgQueue) {
        this.rmqMsgQueue = rmqMsgQueue;
    }
}
