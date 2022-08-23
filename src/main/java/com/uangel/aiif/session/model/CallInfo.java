package com.uangel.aiif.session.model;

import ai.media.stt.SttConverter;
import ai.media.tts.TtsConverter;
import com.uangel.aiif.session.state.CallState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author dajin kim
 */
public class CallInfo {
    private static final Logger log = LoggerFactory.getLogger(CallInfo.class);
    private final ReentrantLock lock = new ReentrantLock();

    private final String callId;
    private final long createTime;

    // State
    private CallState callState;

    // RTP
    private int rtpPort;

    private int samplingRate;

    // TTS & STT
    private String filePath;
    private String resultTxt;
    private TtsConverter ttsConverter;
    private SttConverter sttConverter;

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

    public String getCallId() {
        return callId;
    }
    public long getCreateTime() {
        return createTime;
    }

    public String getLogHeader() {
        return logHeader;
    }
    private void setLogHeader() {
        this.logHeader = "() ("+ this.callId + ") () ";
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

    public int getSamplingRate() {
        return samplingRate;
    }
    public void setSamplingRate(int samplingRate) {
        this.samplingRate = samplingRate;
    }

    // RTP
    public int getRtpPort() {
        return rtpPort;
    }
    public void setRtpPort(int rtpPort) {
        this.rtpPort = rtpPort;
    }

    // TTS & STT
    public String getFilePath() {
        return filePath;
    }
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getResultTxt() {
        return resultTxt;
    }
    public void setResultTxt(String resultTxt) {
        this.resultTxt = resultTxt;
    }

    public TtsConverter getTtsConverter() {
        return ttsConverter;
    }
    public void setTtsConverter(TtsConverter ttsConverter) {
        this.ttsConverter = ttsConverter;
    }

    public SttConverter getSttConverter() {
        return sttConverter;
    }
    public void setSttConverter(SttConverter sttConverter) {
        this.sttConverter = sttConverter;
    }
}
