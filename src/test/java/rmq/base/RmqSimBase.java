package rmq.base;

import com.google.protobuf.util.JsonFormat;
import com.google.protobuf.util.JsonFormat.Printer;
import com.uangel.aiif.config.AiifConfig;
import com.uangel.aiif.rmq.RmqManager;
import com.uangel.aiif.rmq.handler.RmqConsumer;
import com.uangel.aiif.rmq.module.RmqClient;
import com.uangel.aiif.rtpcore.service.NettyChannelManager;
import com.uangel.aiif.service.AppInstance;
import com.uangel.aiif.service.schedule.IntervalTaskManager;
import com.uangel.aiif.session.CallManager;
import com.uangel.protobuf.Message;
import com.uangel.protobuf.MessageBuilder;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.function.Consumer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;

/**
 * @author kangmoo Heo
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({RmqManager.class})
@PowerMockIgnore({"javax.management.*", "javax.xml.*", "org.w3c.*", "org.apache.apache._", "com.sun.*", "com.sun.org.apache.xerces.*", "org.apache.http.conn.ssl.", "javax.net.ssl.", "javax.crypto.*"})
@Slf4j
public class RmqSimBase {

    public RmqSimBase() {
        try {
            appInstance.setConfigPath(new File("src/test/resources/config").getAbsolutePath()+ File.separator);
            appInstance.setConfig(new AiifConfig(appInstance.getConfigPath()));
            // Modify Media File Path
            appInstance.getConfig().setMediaFilePath(System.getProperty("user.dir") + "/src/test/resources/tts/");

            // config
            RmqManager.getInstance().start();

            // IntervalTaskManager.getInstance().init();
            // IntervalTaskManager.getInstance().start();

            makeRmqManagerMock();
            MessageBuilder.setDefaultMsgFrom("T_AIM");
            NettyChannelManager.getInstance().openRtpServer();
            rmqConsumer = new RmqConsumer(new ArrayBlockingQueue<>(1));
            rmqMsgHandler = rmqConsumer.getClass().getDeclaredMethod("messageProcessing", Message.class);
        } catch (Exception e){
            log.warn("ERR Occurs", e);
            Assert.fail();
        }
    }

    protected static final AppInstance appInstance = AppInstance.getInstance();
    protected static final Printer jsonPrinter = JsonFormat.printer().includingDefaultValueFields();
    protected static final CallManager callManager = CallManager.getInstance();
    private RmqConsumer rmqConsumer;
    public static byte[] lastSendRmqMsgByte;
    public static Message lastSendRmqMsg;
    public static List<Message> sendMsgList = new ArrayList<>();
    public static Consumer<byte[]> onRmqMsgResponse = s -> {
        int reasonCode = lastSendRmqMsg.getHeader().getReasonCode();
        Assert.assertTrue(0 == reasonCode || 200 == reasonCode);
    };

    public static Method rmqMsgHandler;

    public static void makeRmqManagerMock() {
        PowerMockito.mockStatic(RmqManager.class);
        RmqManager rmqManager = mock(RmqManager.class);
        BDDMockito.given(RmqManager.getInstance()).willReturn(rmqManager);

        RmqClient mockRmqClient = mock(RmqClient.class);
        Mockito.when(mockRmqClient.getQueueName()).thenReturn("MOCKED_RMQ");
        Mockito.when(mockRmqClient.send(any(byte[].class))).then((invocationOnMock) -> {
            lastSendRmqMsgByte = invocationOnMock.getArgument(0);
            lastSendRmqMsg = Message.parseFrom(lastSendRmqMsgByte);
            sendMsgList.add(lastSendRmqMsg);
            onRmqMsgResponse.accept(lastSendRmqMsgByte);
            return true;
        });

        Mockito.when(rmqManager.getRmqClient(anyString())).thenReturn(mockRmqClient);
    }

    protected void handleMessage(MessageBuilder message){
        handleMessage(message.build());
    }
    protected void handleMessage(Message message){
        try {
            rmqMsgHandler.setAccessible(true);
            rmqMsgHandler.invoke(rmqConsumer, message);
            rmqMsgHandler.setAccessible(false);
        }catch (Exception e){
            log.error("Fail to handle Msg [{}]", message);
            Assert.fail();
        }
    }
}
