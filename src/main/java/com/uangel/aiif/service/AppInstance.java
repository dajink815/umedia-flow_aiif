package com.uangel.aiif.service;

import com.uangel.aiif.config.AiifConfig;
import com.uangel.protobuf.Message;

import java.util.concurrent.BlockingQueue;

/**
 * @author dajin kim
 */
public class AppInstance {
    private static AppInstance instance = null;

    private String configPath = null;
    private AiifConfig aiifConfig = null;

    // RMQ
    private BlockingQueue<Message> rmqMsgQueue;
    private boolean rmqConnect;
    private boolean rmqBlocked;

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

    public AiifConfig getConfig() {
        return aiifConfig;
    }
    public void setConfig(AiifConfig aiifConfig) {
        this.aiifConfig = aiifConfig;
    }

    public BlockingQueue<Message> getRmqMsgQueue() {
        return rmqMsgQueue;
    }
    public void setRmqMsgQueue(BlockingQueue<Message> rmqMsgQueue) {
        this.rmqMsgQueue = rmqMsgQueue;
    }

    public boolean isRmqConnect() {
        return rmqConnect;
    }
    public void setRmqConnect(boolean rmqConnect) {
        this.rmqConnect = rmqConnect;
    }

    public boolean isRmqBlocked() {
        return rmqBlocked;
    }
    public void setRmqBlocked(boolean rmqBlocked) {
        this.rmqBlocked = rmqBlocked;
    }
}
