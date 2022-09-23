package com.uangel.aiif.rmq.handler.aiwf.incoming;

import ai.media.tts.TtsConverter;
import com.uangel.aiif.ai.google.tts.TtsFileManager;
import com.uangel.aiif.ai.google.tts.TtsType;
import com.uangel.aiif.rmq.handler.RmqIncomingMessage;
import com.uangel.aiif.rmq.handler.RmqMsgSender;
import com.uangel.aiif.service.AppInstance;
import com.uangel.aiif.service.ServiceDefine;
import com.uangel.aiif.session.CallManager;
import com.uangel.aiif.session.model.CallInfo;
import com.uangel.aiif.util.FileUtil;
import com.uangel.protobuf.Message;
import com.uangel.protobuf.TtsStartReq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.uangel.aiif.rmq.common.RmqMsgType.*;

/**
 * @author dajin kim
 */
public class RmqTtsStartReq extends RmqIncomingMessage<TtsStartReq> {
    static final Logger log = LoggerFactory.getLogger(RmqTtsStartReq.class);
    private static final CallManager callManager = CallManager.getInstance();
    private static final TtsFileManager ttsFileManager = TtsFileManager.getInstance();
    private static final String MEDIA_DIR = "/media/";
    public static final String CACHE_DIR = "/cache/";

    public RmqTtsStartReq(Message message) {
        super(message);
    }

    @Override
    public void handle() {

        RmqMsgSender sender = RmqMsgSender.getInstance();

        String callId = body.getCallId();
        String dialogId = body.getDialogId();
        CallInfo callInfo = callManager.getCallInfo(callId);
        if (callInfo == null) {
            log.warn("() ({}) () TtsStartReq Fail Find Session", callId);
            // Send Fail Response
            sender.sendTtsStartRes(getTId(), REASON_CODE_NO_SESSION, REASON_NO_SESSION, callId, dialogId);
            return;
        }

        // 파일 새로 생성 하는 경우만 converter Null 체크
        TtsConverter ttsConverter = callInfo.getTtsConverter();
        if (ttsConverter == null) {
            log.warn("{}TtsStartReq TtsConverter is Null", callInfo.getLogHeader());
            // Send Fail Response
            sender.sendTtsStartRes(getTId(), REASON_CODE_AI_ERROR, REASON_AI_ERROR, callId, dialogId);
            return;
        }

        // Clearing 체크
        try {
            log.debug("{}TtsStartReq - CallInfo Lock", callInfo.getLogHeader());
            callInfo.lock();

            if (callInfo.isClearing()) {
                log.warn("() ({}) () TtsStartReq Session is Clearing", callId);
                // Send Fail Response
                sender.sendTtsStartRes(getTId(), REASON_CODE_NO_SESSION, REASON_NO_SESSION, callId, dialogId);
                return;
            }

        } finally {
            callInfo.unlock();
        }


        callInfo.setTtsDialogId(dialogId);
        log.debug("{}TtsStartReq TTS Start - TtsConverter [{}] DialogId [{}]", callInfo.getLogHeader(), ttsConverter.hashCode(), dialogId);

        // TTS Start
        TtsType ttsType = TtsType.getTypeEnum(body.getType());
        String content = body.getContent();
        String mediaPlayPath = null;

        String fileBasePath = AppInstance.getInstance().getConfig().getMediaFilePath();

        // 1. File Type
        if (TtsType.FILE.equals(ttsType)) {
            log.debug("{}TtsStartReq FILE Type : {}", callInfo.getLogHeader(), content);
            // 파일 서치 경로 -> BaseDir / MEDIA_DIR / AIWF 에서 전달 받은 파일 명

            // 파일 존재 하지 않으면 Fail Response
            if (!FileUtil.isExist(fileBasePath + MEDIA_DIR + content)) {
                log.warn("{}TtsStartReq [{}] File Not Exist", callInfo.getLogHeader(), fileBasePath + MEDIA_DIR + content);
                sender.sendTtsStartRes(getTId(), REASON_CODE_MEDIA_ERROR, REASON_MEDIA_ERROR, callId, dialogId);
                return;
            }
            // AIM 에 경로 전달 시, BaseDir 없이
            mediaPlayPath = MEDIA_DIR + content;

        }
        // 2. Ment Type
        else if (TtsType.MENT.equals(ttsType)) {
            // map 에서 파일 조회 (BaseDir 없이 -> /CACHE_DIR/FileName 으로 존재)
            String cachedPath = ttsFileManager.getTtsFileName(content);
            log.debug("{}TtsStartReq MENT Type : {}", callInfo.getLogHeader(), cachedPath);

            // 2-1. 존재 하면 기존 파일 경로 그대로 AIM 에 재생 요청
            if (cachedPath != null) {
                mediaPlayPath = cachedPath;
            }
            // 2-2. 없다면 디렉토리 조회 or 음원 파일 생성
            else {
                // 파일명 규칙 - 멘트 hashCode (AIM 에 경로 전달 시, BaseDir 없이)
                mediaPlayPath = CACHE_DIR + content.hashCode() + ServiceDefine.MEDIA_FILE_EXTENSION.getStr();
                String absolutePath = fileBasePath + mediaPlayPath;

                // 2-2-1. 파일만 존재, map 에 정보 추가
                if (FileUtil.isExist(absolutePath)) {
                    log.debug("{}TtsStartReq File [{}] Already Exist", callInfo.getLogHeader(), mediaPlayPath);
                    ttsFileManager.addTtsFile(content, mediaPlayPath);
                }
                // 2-2-2. content 를 wav 파일로 변환 하여 AIM 에 재생 요청
                else {
                    log.debug("{}TtsStartReq Convert Text({}) to File : {}", callInfo.getLogHeader(), content, mediaPlayPath);

                    try {
                        // TtsConverter 이용해 멘트를 byte array 변환
                        byte[] data = ttsConverter.convertText(content).toByteArray();
                        // byte array 를 wav 파일로 변환
                        FileUtil.byteArrayToFile(data, absolutePath); // todo 파일 변환 에러?
                        // 멘트, 파일 이름 메모리에 저장 하여 관리
                        ttsFileManager.addTtsFile(content, mediaPlayPath);
                    } catch (Exception e) {
                        log.error("{}RmqTtsStartRes.convertTts.Exception ", callInfo.getLogHeader(), e);
                        sender.sendTtsStartRes(getTId(), REASON_CODE_MEDIA_ERROR, REASON_MEDIA_ERROR, callId, dialogId);
                        return;
                    }
                }
            }

        }
        // 3. Else Type
        else {
            log.warn("{} Unknown TTS Type : {}", callInfo.getLogHeader(), ttsType);
            // Send Fail Response
            sender.sendTtsStartRes(getTId(), REASON_CODE_TYPE_ERROR, REASON_TYPE_ERROR, callId, dialogId);
            return;
        }

        // Response 전송 시점  TTS 처리 전?
        // Send Success Response
        sender.sendTtsStartRes(getTId(), callInfo);
        // Send MediaPlayReq
        String mediaDialogId = getTId() + "_" + ttsType.toString();
        sender.sendMediaPlayReq(callInfo, mediaPlayPath, mediaDialogId);
    }


}
