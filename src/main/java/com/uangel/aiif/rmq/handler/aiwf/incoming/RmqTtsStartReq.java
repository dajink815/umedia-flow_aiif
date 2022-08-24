package com.uangel.aiif.rmq.handler.aiwf.incoming;

import ai.media.tts.TtsConverter;
import com.uangel.aiif.ai.google.tts.TtsFileManager;
import com.uangel.aiif.ai.google.tts.TtsType;
import com.uangel.aiif.config.AiifConfig;
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
    private static final String MEDIA_DIR = "/tts/";
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
        String filePath = null;

        if (TtsType.FILE.equals(ttsType)) {
            // 파일 경로 처리 필요?

            // 파일 존재 하지 않으면 Fail Response
            if (!FileUtil.isExist(content)) {
                log.warn("{}TtsStartReq [{}] File Not Exist", callInfo.getLogHeader(), content);
                sender.sendTtsStartRes(header.getTId(), REASON_CODE_MEDIA_ERROR, REASON_MEDIA_ERROR, callId);
                return;
            }
            filePath = content;

        } else if (TtsType.MENT.equals(ttsType)) {
            // 기존에 만들어 둔 파일 존재 하는지 확인 - 멘트 hashCode 로 파일명 조회
            int key = content.hashCode();
            String ttsFile = ttsFileManager.getTtsFileName(key);

            // 1. 존재 하면 기존 파일 경로 그대로 AIM 에 재생 요청
            if (ttsFile != null) {
                filePath = ttsFile;
            }
            // 2. 없다면 content 를 wav 파일로 변환 하여 AIM 에 재생 요청
            else {
                // 파일명 규칙
                String fileName = callId + ServiceDefine.MEDIA_FILE_EXTENSION.getStr();
                AiifConfig config = AppInstance.getInstance().getConfig();
                filePath = config.getMediaFilePath() + CACHE_DIR + fileName;

                // 2-1. TtsConverter 이용해 멘트를 byte array 변환
                byte[] data = ttsConverter.convertText(content).toByteArray();
                // 2-2. byte array 를 wav 파일로 변환
                FileUtil.byteArrayToFile(data, filePath);
                // 2-3. 멘트, 파일 이름 저장 하여 관리
                ttsFileManager.addTtsFile(key, filePath);
            }

        } else {
            log.warn("{} Unknown TTS Type : {}", callInfo.getLogHeader(), ttsType);
        }

        // Response 전송 시점  TTS 처리 전?
        // Send Success Response
        sender.sendTtsStartRes(header.getTId(), callInfo);
        // Send MediaPlayReq
        sender.sendMediaPlayReq(callInfo, filePath);
    }


}
