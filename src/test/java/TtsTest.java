import ai.media.tts.TtsConverter;
import com.google.cloud.texttospeech.v1.*;
import com.google.protobuf.ByteString;
import com.uangel.aiif.service.ServiceDefine;
import com.uangel.aiif.util.FileUtil;
import org.junit.Test;

import java.io.*;

/**
 * Google Cloud TextToSpeech API sample application. Example usage: mvn package exec:java
 * -Dexec.mainClass='com.example.texttospeech.QuickstartSample'
 */
public class TtsTest {

    /** Demonstrates using the Text-to-Speech API. */
    @Test
    public void googleConnect() {
        // Instantiates a client
        try (TextToSpeechClient textToSpeechClient = TextToSpeechClient.create()) {
            // Set the text input to be synthesized
            SynthesisInput input = SynthesisInput.newBuilder().setText("안녕하세요. 유엔젤피자 수내점입니다. 무엇을 도와드릴까요?").build();

            // Build the voice request, select the language code ("en-US") and the ssml voice gender
            // ("neutral")
            VoiceSelectionParams voice =
                    VoiceSelectionParams.newBuilder()
                            .setLanguageCode("ko-KR")
                            .setSsmlGender(SsmlVoiceGender.FEMALE)
                            .build();

            // Select the type of audio file you want returned
            AudioConfig audioConfig =
                    AudioConfig.newBuilder().setAudioEncoding(AudioEncoding.MP3).build();

            // Perform the text-to-speech request on the text input with the selected voice parameters and
            // audio file type
            SynthesizeSpeechResponse response =
                    textToSpeechClient.synthesizeSpeech(input, voice, audioConfig);

            // Get the audio contents from the response
            ByteString audioContents = response.getAudioContent();

            // Write the response to the output file.
            String fileName = "hello.wav";
            FileUtil.byteArrayToFile(audioContents.toByteArray(), fileName);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Demonstrates using the Text to Speech client to synthesize text or ssml.
     *
     * @param text the raw text to be synthesized. (e.g., "Hello there!")
     * @throws Exception on TextToSpeechClient Errors.
     */
    public static void synthesizeText(String text) throws Exception {
        // Instantiates a client
        try (TextToSpeechClient textToSpeechClient = TextToSpeechClient.create()) {
            // Set the text input to be synthesized
            SynthesisInput input = SynthesisInput.newBuilder().setText(text).build();

            // Build the voice request
            VoiceSelectionParams voice =
                    VoiceSelectionParams.newBuilder()
                            .setLanguageCode("ko-KR") // languageCode = "en_us"
                            .setSsmlGender(SsmlVoiceGender.FEMALE) // ssmlVoiceGender = SsmlVoiceGender.FEMALE
                            .build();

            // Select the type of audio file you want returned
            AudioConfig audioConfig =
                    AudioConfig.newBuilder()
                            .setAudioEncoding(AudioEncoding.MP3) // MP3 audio.
                            .build();

            // Perform the text-to-speech request
            SynthesizeSpeechResponse response =
                    textToSpeechClient.synthesizeSpeech(input, voice, audioConfig);

            // Get the audio contents from the response
            ByteString audioContents = response.getAudioContent();

            // Write the response to the output file.
            try (OutputStream out = new FileOutputStream("test.mp3")) {
                out.write(audioContents.toByteArray());
                System.out.println("Audio content written to file \"test.mp3\"");
            }
        }
    }

    @Test
    public void converterTest() {
        TtsConverter ttsConverter = TtsConverter.newBuilder()
                .setAudioEncoding(com.google.cloud.texttospeech.v1.AudioEncoding.LINEAR16)
                .setSampleRateHertz(8000)
                .setLanguageCode(ServiceDefine.LANG_CODE.getStr())
                // Voice 설정
                .setSsmlGender(SsmlVoiceGender.FEMALE)
                .build();

        String fileName = "greeting.wav";
        String filePath = System.getProperty("user.dir") + "/src/test/resources/tts/cache/" + fileName;

        String content = "안녕하세요. 반갑습니다!";

        // 2-1. TtsConverter 이용해 멘트를 byte array 변환
        byte[] data = ttsConverter.convertText(content).toByteArray();
        // 2-2. byte array 를 wav 파일로 변환
        FileUtil.byteArrayToFile(data, filePath);

    }
}
