package com.uangel.aiif.session;

import com.uangel.aiif.config.AiifConfig;
import com.uangel.aiif.rtpcore.service.NettyChannelManager;
import com.uangel.aiif.rtpcore.service.RtpChannelInfo;
import com.uangel.aiif.service.AppInstance;
import com.uangel.aiif.session.model.CallInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author dajin kim
 */
public class CallManager {
    static final Logger log = LoggerFactory.getLogger(CallManager.class);
    private static final ConcurrentHashMap<String, CallInfo> callInfoMap = new ConcurrentHashMap<>();
    private final NettyChannelManager nettyChannelManager = NettyChannelManager.getInstance();
    private static CallManager callManager = null;
    private final AiifConfig config = AppInstance.getInstance().getConfig();

    private CallManager() {
        // nothing
    }

    public static CallManager getInstance() {
        if (callManager == null) {
            callManager = new CallManager();
        }
        return callManager;
    }

    public CallInfo createCallInfo(String callId) {
        if(callId == null) return null;
        if (callInfoMap.containsKey(callId)) {
            log.error("CallInfo [{}] Exist", callId);
            return null;
        }

        CallInfo callInfo = new CallInfo(callId);
        RtpChannelInfo rtpChannelInfo = nettyChannelManager.allocPort(callInfo);
        if(rtpChannelInfo == null){
            log.warn("CallInfo [{}] Fail to allocate port", callId);
            return null;
        }
        callInfo.setRtpChannelInfo(rtpChannelInfo);

        callInfoMap.put(callId, callInfo);
        log.warn("CallInfo [{}] Created", callId);
        return callInfo;

    }

    public CallInfo getCallInfo(String callId) {
        if(callId == null) return null;
        CallInfo callInfo = callInfoMap.get(callId);
        if (callInfo == null) {
            log.warn("CallInfo [{}] Null", callId);
        }
        return callInfo;
    }

    public boolean deleteCallInfo(String callId) {
        if (callId == null) return false;
        boolean result = false;
        CallInfo callInfo = callInfoMap.remove(callId);
        if (callInfo != null) {
            callInfo.dealloc();
            log.warn("CallInfo [{}] Removed", callId);
            result = true;
        }
        return result;
    }

    public ConcurrentMap<String, CallInfo> getCallInfoMap() {
        return callInfoMap;
    }

    public int getCallInfoSize() {
        return callInfoMap.size();
    }
}
