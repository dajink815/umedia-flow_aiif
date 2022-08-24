package rmq;

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
    }
}
