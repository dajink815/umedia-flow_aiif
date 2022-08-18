import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import com.uangel.protobuf.CallCloseReq;
import com.uangel.protobuf.Header;
import com.uangel.protobuf.Message;
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
        Message rmqMessage = Message.newBuilder()
                .setHeader(Header.newBuilder()
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
        Message rmq_message2 = Message.parseFrom(data);

        String jsonString= JsonFormat.printer().includingDefaultValueFields().print(rmq_message2);
        System.out.println(jsonString);

        switch(rmq_message2.getBodyCase().getNumber()){
            case Message.MHBREQ_FIELD_NUMBER:

                break;
            case Message.MHBRES_FIELD_NUMBER:

                break;
            case Message.CALLCLOSEREQ_FIELD_NUMBER:
                Header header = rmq_message2.getHeader();
                CallCloseReq req = rmq_message2.getCallCloseReq();
                System.out.println(header);
                System.out.println(header.getReasonCode());
                System.out.println(req);
                System.out.println(req.getCallId());
                break;
            default:
                System.err.println("Not Defined Type - " + rmq_message2.getBodyCase().getNumber());
                break;
        }
    }

    public static boolean isNull(Object obj) {
        return obj == null || obj.toString().isEmpty();
    }

    @Test
    public void test() {
        System.out.println(System.getProperty("user.dir"));
    }

}
