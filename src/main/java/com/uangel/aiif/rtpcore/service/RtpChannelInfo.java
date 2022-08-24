package com.uangel.aiif.rtpcore.service;


import com.uangel.aiif.session.model.CallInfo;
import io.netty.channel.Channel;
import lombok.Data;
import lombok.NonNull;

/**
 * Netty의 Channel정보를 담는 클래스.
 *
 * @author kangmoo Heo
 */
@Data
public class RtpChannelInfo {
    // RTP를 수신할 포트
    private final int usingPort;
    // RTP를 수신할 채널
    @NonNull
    private final Channel channel;

    @NonNull
    private final CallInfo callInfo;

    private boolean onClosing;

    public RtpChannelInfo(Channel channel, CallInfo callInfo, int usingPort) {
        this.channel = channel;
        this.callInfo = callInfo;
        this.usingPort = usingPort;
        this.onClosing = false;
    }
}
