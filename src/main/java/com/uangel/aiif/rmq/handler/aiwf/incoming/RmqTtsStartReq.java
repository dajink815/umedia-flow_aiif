package com.uangel.aiif.rmq.handler.aiwf.incoming;

import ai.media.tts.TtsConverter;
import com.uangel.aiif.ai.google.tts.TtsFileManager;
import com.uangel.aiif.ai.google.tts.TtsType;
import com.uangel.aiif.rmq.handler.RmqMsgSender;
import com.uangel.aiif.service.AppInstance;
import com.uangel.aiif.service.ServiceDefine;
import com.uangel.aiif.session.CallManager;
import com.uangel.aiif.session.model.CallInfo;
import com.uangel.aiif.util.FileUtil;
import com.uangel.protobuf.Header;
import com.uangel.protobuf.Message;
import com.uangel.protobuf.TtsStartReq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.uangel.aiif.rmq.common.RmqMsgType.*;

/**
 * @author dajin kim
 */
public class RmqTtsStartReq {
    static final Logger log = LoggerFactory.getLogger(RmqTtsStartReq.class);
    private static final CallManager callManager = CallManager.getInstance();
    private static final TtsFileManager ttsFileManager = TtsFileManager.getInstance();
    private static final String MEDIA_DIR = "/media/";
    private static final String CACHE_DIR = "/cache/";

    public RmqTtsStartReq() {
        // nothing
    }

    public void handle(Message msg) {

        Header header = msg.getHeader();
        TtsStartReq req = msg.getTtsStartReq();
        // req check isEmpty

        RmqMsgSender sender = RmqMsgSender.getInstance();

        String callId = req.getCallId();
        CallInfo callInfo = callManager.getCallInfo(callId);
        if (callInfo == null) {
            log.warn("() ({}) () TtsStartReq Fail Find Session", callId);
            // Send Fail Response
            sender.sendTtsStartRes(header.getTId(), REASON_CODE_NO_SESSION, REASON_NO_SESSION, callId);
            return;
        }

        // 파일 새로 생성 하는 경우만 converter Null 체크
        TtsConverter ttsConverter = callInfo.getTtsConverter();
        if (ttsConverter == null) {
            log.warn("{}TtsStartReq TtsConverter is Null", callInfo.getLogHeader());
            // Send Fail Response
            sender.sendTtsStartRes(header.getTId(), REASON_CODE_AI_ERROR, REASON_AI_ERROR, callId);
            return;
        }

        // TTS Start
        TtsType ttsType = TtsType.getTypeEnum(req.getType());
        String content = req.getContent();
        String mediaPlayPath = null;

        String fileBasePath = AppInstance.getInstance().getConfig().getMediaFilePath();

        if (TtsType.FILE.equals(ttsType)) {
            log.debug("{}TtsStartReq FILE Type : {}", callInfo.getLogHeader(), content);
            // 파일 서치 경로 -> BaseDir / MEDIA_DIR / AIWF 에서 전달 받은 파일 명

            // 파일 존재 하지 않으면 Fail Response
            if (!FileUtil.isExist(fileBasePath + MEDIA_DIR + content)) {
                log.warn("{}TtsStartReq [{}] File Not Exist", callInfo.getLogHeader(), fileBasePath + MEDIA_DIR + content);
                sender.sendTtsStartRes(header.getTId(), REASON_CODE_MEDIA_ERROR, REASON_MEDIA_ERROR, callId);
                return;
            }
            // AIM 에 경로 전달 시, BaseDir 없이
            mediaPlayPath = MEDIA_DIR + content;

        } else if (TtsType.MENT.equals(ttsType)) {
            // 기존에 만들어 둔 파일 존재 하는지 확인 - 멘트 hashCode 로 파일명 조회
            int key = content.hashCode();
            // BaseDir 없이 -> / CACHE_DIR / 파일 명으로 존재
            String cachedPath = ttsFileManager.getTtsFileName(key);
            log.debug("{}TtsStartReq MENT Type : {}", callInfo.getLogHeader(), cachedPath);

            // 1. 존재 하면 기존 파일 경로 그대로 AIM 에 재생 요청
            if (cachedPath != null) {
                mediaPlayPath = cachedPath;
            }
            // 2. 없다면 content 를 wav 파일로 변환 하여 AIM 에 재생 요청
            else {
                // todo 파일명 규칙
                String fileName = header.getTId() + ServiceDefine.MEDIA_FILE_EXTENSION.getStr();
                // AIM 에 경로 전달 시, BaseDir 없이
                mediaPlayPath = CACHE_DIR + fileName;
                log.debug("{}TtsStartReq Convert Text({}) to File : {}", callInfo.getLogHeader(), content, mediaPlayPath);

                try {
                    // 2-1. TtsConverter 이용해 멘트를 byte array 변환
                    byte[] data = ttsConverter.convertText(content).toByteArray();
                    // 2-2. byte array 를 wav 파일로 변환
                    FileUtil.byteArrayToFile(data, fileBasePath + mediaPlayPath);
                    // 2-3. 멘트, 파일 이름 저장 하여 관리
                    ttsFileManager.addTtsFile(key, mediaPlayPath);
                } catch (Exception e) {
                    log.error("{}RmqTtsStartRes.convertTts.Exception ", callInfo.getLogHeader(), e);
                    sender.sendTtsStartRes(header.getTId(), REASON_CODE_MEDIA_ERROR, REASON_MEDIA_ERROR, callId);
                    return;
                }
            }

        } else {
            log.warn("{} Unknown TTS Type : {}", callInfo.getLogHeader(), ttsType);
        }

        // Response 전송 시점  TTS 처리 전?
        // Send Success Response
        sender.sendTtsStartRes(header.getTId(), callInfo);
        // Send MediaPlayReq
        sender.sendMediaPlayReq(callInfo, mediaPlayPath);
    }


}
