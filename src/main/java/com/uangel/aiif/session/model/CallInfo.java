package com.uangel.aiif.session.model;

import ai.media.stt.SttConverter;
import ai.media.tts.TtsConverter;
import com.uangel.aiif.rtpcore.service.NettyChannelManager;
import com.uangel.aiif.rtpcore.service.RtpChannelInfo;
import com.uangel.aiif.session.state.CallState;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author dajin kim
 */
@Getter
@Setter
public class CallInfo {
    private static final Logger log = LoggerFactory.getLogger(CallInfo.class);
    protected static final NettyChannelManager nettyChannelManager = NettyChannelManager.getInstance();
    private final ReentrantLock lock = new ReentrantLock();

    private final String callId;
    private final long createTime;

    // State
    private CallState callState;

    private int samplingRate;

    // TTS & STT
    private TtsConverter ttsConverter;
    private SttConverter sttConverter;
    private String ttsDialogId;
    private String sttDialogId;

    private RtpChannelInfo rtpChannelInfo;

    private AtomicBoolean isClearing = new AtomicBoolean(false);

    private String logHeader = "";

    public CallInfo(String callId) {
        this.callId = callId;
        this.createTime = System.currentTimeMillis();
        this.setLogHeader();
        // status
        this.callState = CallState.NEW;
    }


    // lock methods
    public void lock(){
        this.lock.lock();
    }
    public void unlock(){
        this.lock.unlock();
    }
    public void handleLock(Runnable r){
        try{
            this.lock.lock();
            r.run();
        } finally {
            this.lock.unlock();
        }
    }

    public String getLogHeader() {
        return logHeader;
    }
    private void setLogHeader() {
        this.logHeader = "() ("+ this.callId + ") () ";
    }



    public void dealloc(){
        nettyChannelManager.deallocPort(rtpChannelInfo.getUsingPort());
    }

    public CallState getSessionState() {
        return callState;
    }

    public void setSessionState(CallState callState) {
        if (this.callState == null || !this.callState.equals(callState)) {
            log.info("{}SESSION Status Changed [{}] --> [{}]", logHeader, this.callState, callState);
            this.callState = callState;
        }
    }

    // RTP
    public int getRtpPort() {
        return rtpChannelInfo.getUsingPort();
    }

    // IsClearing
    public boolean isClearing() {
        return isClearing.get();
    }
    public void setClearing(boolean isClearing) {
        this.isClearing.set(isClearing);
        log.info("{}SESSION Clearing Flag Changed {} --> {}", logHeader, this.isClearing.get(), isClearing);
    }

}
