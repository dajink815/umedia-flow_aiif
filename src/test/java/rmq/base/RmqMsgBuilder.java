package rmq.base;

import com.uangel.protobuf.*;

/**
 * @author kangmoo Heo
 */
public class RmqMsgBuilder {
    private static final String AIWF_RMQ_NAME = "T_AIWF";
    private static final String AIM_RMQ_NAME = "T_AIM";
    private static final IHbRes.Builder I_HB_RES = IHbRes.newBuilder();
    private static final CreateSessionReq.Builder CREATE_SESSION_REQ = CreateSessionReq.newBuilder();
    private static final TtsStartReq.Builder TTS_START_REQ = TtsStartReq.newBuilder();
    private static final TtsResultRes.Builder TTS_RESULT_RES = TtsResultRes.newBuilder();
    private static final SttStartReq.Builder STT_START_REQ = SttStartReq.newBuilder();
    private static final SttResultRes.Builder STT_RESULT_RES = SttResultRes.newBuilder();
    private static final DelSessionReq.Builder DEL_SESSION_REQ = DelSessionReq.newBuilder();
    private static final MediaStartReq.Builder MEDIA_START_REQ = MediaStartReq.newBuilder();
    private static final MediaPlayRes.Builder MEDIA_PLAY_RES = MediaPlayRes.newBuilder();
    private static final MediaDoneReq.Builder MEDIA_DONE_REQ = MediaDoneReq.newBuilder();
    private static final MediaStopRes.Builder MEDIA_STOP_RES = MediaStopRes.newBuilder();


    public static MessageBuilder I_HB_RES(){return new MessageBuilder().setMsgFrom(AIWF_RMQ_NAME).setBody(I_HB_RES);}
    public static MessageBuilder CREATE_SESSION_REQ(String callId){return new MessageBuilder().setMsgFrom(AIWF_RMQ_NAME).setBody(CREATE_SESSION_REQ.setCallId(callId));}
    public static MessageBuilder TTS_START_REQ(String callId, int type, String content){return new MessageBuilder().setMsgFrom(AIWF_RMQ_NAME).setBody(TTS_START_REQ.setCallId(callId).setType(type).setContent(content));}
    public static MessageBuilder TTS_RESULT_RES(String callId){return new MessageBuilder().setMsgFrom(AIWF_RMQ_NAME).setBody(TTS_RESULT_RES.setCallId(callId));}
    public static MessageBuilder STT_START_REQ(String callId, int duration){return new MessageBuilder().setMsgFrom(AIWF_RMQ_NAME).setBody(STT_START_REQ.setCallId(callId).setDuration(duration));}
    public static MessageBuilder STT_RESULT_RES(String callId){return new MessageBuilder().setMsgFrom(AIWF_RMQ_NAME).setBody(STT_RESULT_RES.setCallId(callId));}
    public static MessageBuilder DEL_SESSION_REQ(String callId){return new MessageBuilder().setMsgFrom(AIWF_RMQ_NAME).setBody(DEL_SESSION_REQ.setCallId(callId));}
    public static MessageBuilder MEDIA_START_REQ(String callId, int samplingRate){return new MessageBuilder().setMsgFrom(AIM_RMQ_NAME).setBody(MEDIA_START_REQ.setCallId(callId).setSamplingRate(samplingRate));}
    public static MessageBuilder MEDIA_PLAY_RES(String callId){return new MessageBuilder().setMsgFrom(AIM_RMQ_NAME).setBody(MEDIA_PLAY_RES.setCallId(callId));}
    public static MessageBuilder MEDIA_DONE_REQ(String callId){return new MessageBuilder().setMsgFrom(AIM_RMQ_NAME).setBody(MEDIA_DONE_REQ.setCallId(callId));}
    public static MessageBuilder MEDIA_STOP_RES(String callId){return new MessageBuilder().setMsgFrom(AIM_RMQ_NAME).setBody(MEDIA_STOP_RES.setCallId(callId));}
}
