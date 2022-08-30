package com.uangel.aiif.config;

import com.uangel.aiif.util.StringUtil;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dajin kim
 */
@Getter
@Setter
public class AiifConfig extends DefaultConfig {
    static final Logger log = LoggerFactory.getLogger(AiifConfig.class);

    // SECTION
    private static final String SECTION_COMMON = "COMMON";
    private static final String SECTION_RMQ = "RMQ";
    private static final String SECTION_NETTY = "NETTY";
    private static final String SECTION_AI = "AI";

    // FIELD - COMMON
    private static final String FIELD_HB_INTERVAL = "HB_INTERVAL";
    // FIELD - RMQ
    private static final String FIELD_AIS = "AIS";
    private static final String FIELD_AIM = "AIM";
    private static final String FIELD_AIWF = "AIWF";
    private static final String FIELD_AIIF = "AIIF";
    private static final String FIELD_HOST = "HOST";
    private static final String FIELD_USER = "USER";
    private static final String FIELD_PORT = "PORT";
    private static final String FIELD_PASS = "PASS";
    private static final String FIELD_AIWF_HOST = "AIWF_HOST";
    private static final String FIELD_AIWF_USER = "AIWF_USER";
    private static final String FIELD_AIWF_PORT = "AIWF_PORT";
    private static final String FIELD_AIWF_PASS = "AIWF_PASS";
    private static final String FIELD_THREAD_SIZE = "THREAD_SIZE";
    private static final String FIELD_QUEUE_SIZE = "QUEUE_SIZE";
    // FIELD - AI
    private static final String FIELD_MEDIA_FILE_PATH = "MEDIA_FILE_PATH";
    private static final String FIELD_STT_THREAD_SIZE = "STT_THREAD_SIZE";
    private static final String FIELD_LONG_FILE = "LONG_FILE";

    // VALUE - COMMON
    private int hbInterval;
    // VALUE - RMQ
    private String ais;
    private String aim;
    private String aiwf;
    private String aiif;
    private String host;
    private String user;
    private int port;
    private String pass;
    private String aiwfHost;
    private String aiwfUser;
    private int aiwfPort;
    private String aiwfPass;
    private int rmqThreadSize;
    private int rmqQueueSize;
    // VALUE - NETTY
    private String serverIp;
    private int rmqBufferCount;
    private int rmqConsumerCount;
    private int maxMessagesPerSec;
    private int nettyEventLoopThreadCount;
    private int udpRcvBufferSize;
    private int udpSndBufferSize;
    private int localUdpPortMin;
    private int localUdpPortMax;
    // VALUE - AI
    private String mediaFilePath;
    private int sttThreadSize;
    private int longFile;

    public AiifConfig(String configPath) {
        super(configPath);
        boolean result = load(configPath);
        log.info("Load config ... [{}]",  StringUtil.getOkFail(result));
        if (result) {
            loadConfig();
        }
    }

    @Override
    public String getStrValue(String session, String key, String defaultValue) {
        String value = super.getStrValue(session, key, defaultValue);
        log.info("() () () Config Session [{}] key [{}] value [{}]", session, key, value);
        return value;
    }

    @Override
    public int getIntValue(String section, String key, int defaultValue) {
        int value = super.getIntValue(section, key, defaultValue);
        log.info("() () () Config Session [{}] key [{}] value [{}]", section, key, value);
        return value;
    }

    @Override
    public void save() throws ConfigurationException {
        super.save();
    }

    @Override
    public Configuration getWritableConfig() {
        return super.getWritableConfig();
    }

    private void loadConfig() {
        loadCommonConfig();
        loadRmqConfig();
        loadNettyConfig();
        loadAiConfig();
    }

    private void loadCommonConfig() {
        this.hbInterval = getIntValue(SECTION_COMMON, FIELD_HB_INTERVAL, 200);
    }

    private void loadRmqConfig() {
        this.ais = getStrValue(SECTION_RMQ, FIELD_AIS, "");
        this.aim = getStrValue(SECTION_RMQ, FIELD_AIM, "");
        this.aiwf = getStrValue(SECTION_RMQ, FIELD_AIWF, "");
        this.aiif = getStrValue(SECTION_RMQ, FIELD_AIIF, "");
        this.host = getStrValue(SECTION_RMQ, FIELD_HOST, "");
        this.user = getStrValue(SECTION_RMQ, FIELD_USER, "");
        this.port = getIntValue(SECTION_RMQ, FIELD_PORT, 5);
        this.pass = getStrValue(SECTION_RMQ, FIELD_PASS, "");
        this.aiwfHost = getStrValue(SECTION_RMQ, FIELD_AIWF_HOST, "");
        this.aiwfUser = getStrValue(SECTION_RMQ, FIELD_AIWF_USER, "");
        this.aiwfPort = getIntValue(SECTION_RMQ, FIELD_AIWF_PORT, 5);
        this.aiwfPass = getStrValue(SECTION_RMQ, FIELD_AIWF_PASS, "");
        this.rmqThreadSize = getIntValue(SECTION_RMQ, FIELD_THREAD_SIZE, 5);
        this.rmqQueueSize = getIntValue(SECTION_RMQ, FIELD_QUEUE_SIZE, 5);
    }

    private void loadNettyConfig() {
        this.serverIp = getStrValue(SECTION_NETTY, "SERVER_IP", "");
        this.rmqBufferCount = getIntValue(SECTION_NETTY, "RMQ_BUFFER_COUNT", 4096);
        this.rmqConsumerCount = getIntValue(SECTION_NETTY, "RMQ_CONSUMER_COUNT", 128);
        this.maxMessagesPerSec = getIntValue(SECTION_NETTY, "MAX_MESSAGES_PER_SEC", 0);
        this.nettyEventLoopThreadCount = getIntValue(SECTION_NETTY, "NETTY_EVENT_LOOP_THREAD_COUNT", 64);
        this.udpRcvBufferSize = getIntValue(SECTION_NETTY, "UDP_RCV_BUFFER_SIZE", 33554432);
        this.udpSndBufferSize = getIntValue(SECTION_NETTY, "UDP_SND_BUFFER_SIZE", 16777216);
        this.localUdpPortMin = getIntValue(SECTION_NETTY, "LOCAL_UDP_PORT_MIN", 10000);
        this.localUdpPortMax = getIntValue(SECTION_NETTY, "LOCAL_UDP_PORT_MAX", 50000);
    }

    private void loadAiConfig() {
        this.mediaFilePath = getStrValue(SECTION_AI, FIELD_MEDIA_FILE_PATH, "");
        this.sttThreadSize = getIntValue(SECTION_AI, FIELD_STT_THREAD_SIZE, 10);
        this.longFile = getIntValue(SECTION_AI, FIELD_LONG_FILE, 168);
    }
}
