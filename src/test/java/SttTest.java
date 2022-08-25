import ai.media.stt.SttConverter;
import com.google.api.gax.rpc.ClientStream;
import com.google.api.gax.rpc.ResponseObserver;
import com.google.api.gax.rpc.StreamController;
import com.google.cloud.speech.v1.*;
import com.google.protobuf.ByteString;
import com.uangel.aiif.service.ServiceDefine;
import com.uangel.aiif.util.SleepUtil;
import org.junit.Test;
// Imports the Google Cloud client library
import com.google.cloud.speech.v1.RecognitionConfig.AudioEncoding;

import javax.sound.sampled.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author dajin kim
 */
public class SttTest {

    /** Demonstrates using the Speech API to transcribe an audio file. */
    @Test
    public void googleStt() throws IOException {
        // Instantiates a client
        try (SpeechClient speechClient = SpeechClient.create()) {

            // The path to the audio file to transcribe
            String gcsUri = "gs://cloud-samples-data/speech/brooklyn_bridge.raw";

            // Builds the sync recognize request
            RecognitionConfig config =
                    RecognitionConfig.newBuilder()
                            .setEncoding(AudioEncoding.LINEAR16)
                            .setSampleRateHertz(16000)
                            .setLanguageCode("ko-KR")
                            .build();

            // Set Content with ByteArray
            //ByteString bytes = ByteString.copyFrom(new byte[5]);
            //RecognitionAudio audio = RecognitionAudio.newBuilder().setContent(bytes).build();

            RecognitionAudio audio = RecognitionAudio.newBuilder().setUri(gcsUri).build();

            // Performs speech recognition on the audio file
            RecognizeResponse response = speechClient.recognize(config, audio);
            List<SpeechRecognitionResult> results = response.getResultsList();

            for (SpeechRecognitionResult result : results) {
                // There can be several alternative transcripts for a given chunk of speech.
                // Just use the first (most likely) one here.
                SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
                System.out.printf("Transcription: %s%n", alternative.getTranscript());
            }
        }
    }

    @Test
    public void googleStt2() {
        ResponseObserver<StreamingRecognizeResponse> responseObserver = null;
        try (SpeechClient client = SpeechClient.create()) {

            responseObserver = new ResponseObserver<StreamingRecognizeResponse>() {
                ArrayList<StreamingRecognizeResponse> responses = new ArrayList<>();

                public void onStart(StreamController controller) {
                }

                public void onResponse(StreamingRecognizeResponse response) {
                    responses.add(response);
                }

                public void onComplete() {
                    String record = "";

                    for (StreamingRecognizeResponse response : responses) {
                        StreamingRecognitionResult result = response.getResultsList().get(0);
                        SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
                        record += alternative.getTranscript();
                        System.out.printf("Transcript : %s\n", alternative.getTranscript());
                    }
                }

                public void onError(Throwable t) {
                    System.out.println(t);
                }
            };

            ClientStream<StreamingRecognizeRequest> clientStream = client.streamingRecognizeCallable()
                    .splitCall(responseObserver);

            RecognitionConfig recognitionConfig = RecognitionConfig.newBuilder()
                    .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16).setLanguageCode("ko-KR")// 한국어 설정
                    .setSampleRateHertz(16000).build();
            StreamingRecognitionConfig streamingRecognitionConfig = StreamingRecognitionConfig.newBuilder()
                    .setConfig(recognitionConfig).build();

            StreamingRecognizeRequest request = StreamingRecognizeRequest.newBuilder()
                    .setStreamingConfig(streamingRecognitionConfig).build(); // The first request in a streaming call
            // has to be a config

            clientStream.send(request);
            // SampleRate:16000Hz, SampleSizeInBits: 16, Number of channels: 1, Signed:
            // true,
            // bigEndian: false
            AudioFormat audioFormat = new AudioFormat(16000, 16, 1, true, false);
            TargetDataLine.Info targetInfo = new TargetDataLine.Info(TargetDataLine.class, audioFormat); // Set the system information to
            // read from the microphone audio
            // stream

            if (!AudioSystem.isLineSupported(targetInfo)) {
                System.out.println("Microphone not supported");
                System.exit(0);
            }
            // Target data line captures the audio stream the microphone produces.
            TargetDataLine targetDataLine = (TargetDataLine) AudioSystem.getLine(targetInfo);
            targetDataLine.open(audioFormat);
            targetDataLine.start();
            System.out.println("Start speaking");
            long startTime = System.currentTimeMillis();
            // Audio Input Stream
            AudioInputStream audio = new AudioInputStream(targetDataLine);
            while (true) {
                long estimatedTime = System.currentTimeMillis() - startTime;
                byte[] data = new byte[6400];
                audio.read(data);
                if (estimatedTime > 6000) { // 60 seconds 스트리밍 하는 시간설정(기본 60초)
                    System.out.println("Stop speaking.");
                    targetDataLine.stop();
                    targetDataLine.close();
                    break;
                }
                request = StreamingRecognizeRequest.newBuilder().setAudioContent(ByteString.copyFrom(data)).build();
                clientStream.send(request);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        responseObserver.onComplete(); //완료 이벤트 발생
    }

    @Test
    public void converterTest() {
        SttConverter sttConverter = SttConverter.newBuilder()
                .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                .setSampleRateHertz(8000)
                .setLanguageCode(ServiceDefine.LANG_CODE.getStr())
                .build();

        sttConverter.start();

        SleepUtil.trySleep(5000);

        sttConverter.stop();

        System.out.println(System.getenv("GOOGLE_APPLICATION_CREDENTIALS"));

    }
}

