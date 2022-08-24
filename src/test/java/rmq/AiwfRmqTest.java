package rmq;

import com.uangel.aiif.util.SleepUtil;
import org.junit.Assert;
import org.junit.Test;
import rmq.base.RmqSimBase;

/**
 * @author kangmoo Heo
 */
import static rmq.base.RmqMsgBuilder.*;
public class AiwfRmqTest extends RmqSimBase {
    String callId = "TEST_CALL_ID";

    @Test
    public void test(){
        handleMessage(CREATE_SESSION_REQ(callId));
        Assert.assertEquals(1, callManager.getCallInfoSize());
        Assert.assertEquals(callId, lastSendRmqMsg.getCreateSessionRes().getCallId());

        handleMessage(MEDIA_START_REQ(callId, 8000));
        handleMessage(TTS_START_REQ(callId, 0, System.getProperty("user.dir") + "/src/test/resources/tts/cache/greeting.wav"));

        SleepUtil.trySleep(300);
        handleMessage(MEDIA_PLAY_RES(callId));

        SleepUtil.trySleep(1000);
        handleMessage(MEDIA_DONE_REQ(callId));
        SleepUtil.trySleep(300);
        handleMessage(TTS_RESULT_RES(callId));

        SleepUtil.trySleep(1000);
        handleMessage(STT_START_REQ(callId, 5000));
    }
}
