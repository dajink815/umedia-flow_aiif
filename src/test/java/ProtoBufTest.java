import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import com.uangel.rmq.message.CallCloseReq;
import com.uangel.rmq.message.RmqHeader;
import com.uangel.rmq.message.RmqMessage;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * @author dajin kim
 */
public class ProtoBufTest {

    @Test
    public void callCloseTest() throws InvalidProtocolBufferException {
        RmqMessage rmqMessage = RmqMessage.newBuilder()
                .setHeader(RmqHeader.newBuilder()
                        .setType("CALL_CLOSE_REQ")
                        .setTId(UUID.randomUUID().toString())
                        .setMsgFrom("AI_AIWF")
                        .setReason("success")
                        .setReasonCode(0)
                        .setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")))
                        .build())
                .setCallCloseReq(CallCloseReq.newBuilder()
                        .setCallId("ABCD"))
                .build();

        System.out.println(">> RmqMessage : \r\n" + rmqMessage);
        System.out.println(">> JsonFormat : \r\n" + JsonFormat.printer().includingDefaultValueFields().print(rmqMessage));

        // RmqMessage -> ByteArray (RabbitMQ 송신)
        byte[] data = rmqMessage.toByteArray();

        // ByteArray -> RmqMessage (RabbitMQ 수신)
        RmqMessage rmq_message2 = RmqMessage.parseFrom(data);

        String jsonString= JsonFormat.printer().includingDefaultValueFields().print(rmq_message2);
        //System.out.println(jsonString);

        switch(rmq_message2.getBodyCase().getNumber()){
            case RmqMessage.MHBREQ_FIELD_NUMBER:

                break;
            case RmqMessage.MHBRES_FIELD_NUMBER:

                break;
            case RmqMessage.CALLCLOSEREQ_FIELD_NUMBER:
                System.out.println("CallCloseReq - " + RmqMessage.CALLCLOSEREQ_FIELD_NUMBER);
                RmqHeader header = rmq_message2.getHeader();
                CallCloseReq req = rmq_message2.getCallCloseReq();
                System.out.println(req.getCallId());
                break;
            default:
                System.err.println("Not Defined Type - " + rmq_message2.getBodyCase().getNumber());
                break;
        }
    }

    @Test
    public void test() {
        System.out.println(System.getProperty("user.dir"));
    }

}
