package com.uangel.aiif.rmq.handler.aim.incoming;

import ai.media.stt.SttConverter;
import ai.media.tts.TtsConverter;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.texttospeech.v1.SsmlVoiceGender;
import com.uangel.aiif.rmq.handler.RmqIncomingMessage;
import com.uangel.aiif.rmq.handler.RmqMsgSender;
import com.uangel.aiif.service.ServiceDefine;
import com.uangel.aiif.session.CallManager;
import com.uangel.aiif.session.model.CallInfo;
import com.uangel.protobuf.MediaStartReq;
import com.uangel.protobuf.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.uangel.aiif.rmq.common.RmqMsgType.REASON_CODE_NO_SESSION;
import static com.uangel.aiif.rmq.common.RmqMsgType.REASON_NO_SESSION;

/**
 * @author dajin kim
 */
public class RmqMediaStartReq extends RmqIncomingMessage<MediaStartReq> {
    static final Logger log = LoggerFactory.getLogger(RmqMediaStartReq.class);
    private static final CallManager callManager = CallManager.getInstance();

    public RmqMediaStartReq(Message message) {
        super(message);
    }

    @Override
    public void handle() {
        // RTP Port 할당 -> AIM 전달

        RmqMsgSender sender = RmqMsgSender.getInstance();

        String callId = body.getCallId();
        CallInfo callInfo = callManager.getCallInfo(callId);
        if (callInfo == null) {
            log.warn("() ({}) () MediaStartReq Fail Find Session", callId);
            // Send Fail Response
            sender.sendMediaStartRes(getTId(), REASON_CODE_NO_SESSION, REASON_NO_SESSION, callId);
            return;
        }

        // Sampling Rate
        int samplingRate = body.getSamplingRate();
        if (samplingRate == 0) {
            log.warn("{}MediaStartReq SamplingRate is Null", callInfo.getLogHeader());
            // Default SamplingRate
            samplingRate = 8000;
        }
        callInfo.setSamplingRate(samplingRate);

        SttConverter sttConverter = SttConverter.newBuilder()
                .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                .setSampleRateHertz(samplingRate)
                .setLanguageCode(ServiceDefine.LANG_CODE.getStr())
                .build();

        TtsConverter ttsConverter = TtsConverter.newBuilder()
                .setAudioEncoding(com.google.cloud.texttospeech.v1.AudioEncoding.LINEAR16)
                .setSampleRateHertz(samplingRate)
                .setLanguageCode(ServiceDefine.LANG_CODE.getStr())
                // Voice 설정
                .setSsmlGender(SsmlVoiceGender.NEUTRAL)
                .build();

        log.debug("{}MediaStartReq - STT/TTS Converter Created (STT: {}/TTS: {})", callInfo.getLogHeader(), sttConverter.hashCode(), ttsConverter.hashCode());

        callInfo.setSttConverter(sttConverter);
        callInfo.setTtsConverter(ttsConverter);

        sender.sendMediaStartRes(getTId(), callInfo);

    }
}
