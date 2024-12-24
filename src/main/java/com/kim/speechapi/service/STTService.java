package com.kim.speechapi.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.speech.v1.*;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class STTService {

    private SpeechSettings speechSettings;

    public STTService() {
        try {
            // Set the credentials programmatically
            String credentialsPath = "C:/Users/User/credentials/speechapi-445702-1d004d984d57.json"; // Update with your path
            GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(credentialsPath));
            this.speechSettings = SpeechSettings.newBuilder().setCredentialsProvider(() -> credentials).build();
            System.out.println("Google Cloud credentials successfully loaded!");
        } catch (Exception e) {
            throw new RuntimeException("Failed to set up Google Cloud credentials: " + e.getMessage());
        }
    }

    public String transcribeAudio(String filePath) {
        try (SpeechClient speechClient = SpeechClient.create(this.speechSettings)) {
            // Load the audio file into memory
            Path path = Paths.get(filePath);
            byte[] audioBytes = Files.readAllBytes(path);

            // 발화지연표현
            SpeechContext speechContext = SpeechContext.newBuilder()
                    .addPhrases("음")
                    .addPhrases("흠")
                    .addPhrases("어")
                    .addPhrases("음음")
                    .addPhrases("umm")
                    .addPhrases("hmm")
                    .addPhrases("uh")
                    .addPhrases("uhh")
                    .setBoost(10.0f)     // Adjust boost val 10-20
                    .build();

            // Configure recognition settings
            RecognitionConfig config = RecognitionConfig.newBuilder()
                    .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                    .setSampleRateHertz(48000)
                    .setLanguageCode("ko-KR") //한국어
                    .addSpeechContexts(speechContext) // 위에 단어 음정 추가한거
                    .setEnableAutomaticPunctuation(true) // 발화지연표현
                    .build();

            RecognitionAudio audio = RecognitionAudio.newBuilder()
                    .setContent(com.google.protobuf.ByteString.copyFrom(audioBytes))
                    .build();

            // Perform transcription
            RecognizeResponse response = speechClient.recognize(config, audio);
            StringBuilder transcription = new StringBuilder();
            for (SpeechRecognitionResult result : response.getResultsList()) {
                transcription.append(result.getAlternativesList().get(0).getTranscript()).append("\n");
            }

            return transcription.toString();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to transcribe audio: " + e.getMessage());
        }
    }
}
