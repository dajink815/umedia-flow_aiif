package com.uangel.aiif.session.model;

import com.uangel.aiif.session.state.SessionState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author dajin kim
 */
public class SessionInfo {
    private static final Logger log = LoggerFactory.getLogger(SessionInfo.class);
    private final ReentrantLock lock = new ReentrantLock();

    private final String callId;
    private final long createTime;

    // State
    private SessionState sessionState;

    // RTP
    private int rtpPort;

    // TTS & STT
    private String filePath;
    private String resultTxt;

    private String logHeader = "";

    public SessionInfo(String callId) {
        this.callId = callId;
        this.createTime = System.currentTimeMillis();
        this.setLogHeader();
        // status
        this.sessionState = SessionState.NEW;
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

    public SessionState getSessionState() {
        return sessionState;
    }
    public void setSessionState(SessionState sessionState) {
        if (this.sessionState == null || !this.sessionState.equals(sessionState)) {
            log.info("{}SESSION Status Changed [{}] --> [{}]", logHeader, this.sessionState, sessionState);
            this.sessionState = sessionState;
        }
    }

    public int getRtpPort() {
        return rtpPort;
    }
    public void setRtpPort(int rtpPort) {
        this.rtpPort = rtpPort;
    }

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
}
