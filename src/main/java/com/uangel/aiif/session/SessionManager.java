package com.uangel.aiif.session;

import com.uangel.aiif.config.AiifConfig;
import com.uangel.aiif.service.AppInstance;
import com.uangel.aiif.session.model.SessionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author dajin kim
 */
public class SessionManager {
    static final Logger log = LoggerFactory.getLogger(SessionManager.class);
    private static final ConcurrentHashMap<String, SessionInfo> sessionInfoMap = new ConcurrentHashMap<>();
    private static SessionManager callManager = null;
    private final AiifConfig config = AppInstance.getInstance().getConfig();

    private SessionManager() {
        // nothing
    }

    public static SessionManager getInstance() {
        if (callManager == null) {
            callManager = new SessionManager();
        }
        return callManager;
    }

    public SessionInfo createSessionInfo(String callId) {
        if(callId == null) return null;
        if (sessionInfoMap.containsKey(callId)) {
            log.error("SessionInfo [{}] Exist", callId);
            return null;
        }

        SessionInfo sessionInfo = new SessionInfo(callId);
        // Set Field

        sessionInfoMap.put(callId, sessionInfo);
        log.warn("SessionInfo [{}] Created", callId);
        return sessionInfo;

    }

    public SessionInfo getSessionInfo(String callId) {
        if(callId == null) return null;
        SessionInfo sessionInfo = sessionInfoMap.get(callId);
        if (sessionInfo == null) {
            log.warn("SessionInfo [{}] Null", callId);
        }
        return sessionInfo;
    }

    public void deleteSessionInfo(String callId) {
        if (callId == null) return;
        if (sessionInfoMap.remove(callId) != null) {
            log.warn("SessionInfo [{}] Removed", callId);
        }
    }

    public ConcurrentMap<String, SessionInfo> getSessionInfoMap() {
        return sessionInfoMap;
    }

    public int getSessionInfoSize() {
        return sessionInfoMap.size();
    }
}
