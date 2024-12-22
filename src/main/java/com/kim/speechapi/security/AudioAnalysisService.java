package com.kim.speechapi.security;

import org.springframework.stereotype.Service;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;
import java.util.Map;

@Service
public class AudioAnalysisService {

    public double analyzeDecibel(String wavFilePath) {
        try {
            // Load the .wav file
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(wavFilePath));
            byte[] buffer = new byte[1024];
            int bytesRead;
            double rms = 0;

            // Read audio data and calculate RMS
            while ((bytesRead = audioInputStream.read(buffer)) != -1) {
                for (int i = 0; i < bytesRead; i++) {
                    rms += buffer[i] * buffer[i];
                }
            }
            rms = Math.sqrt(rms / buffer.length);

            // Convert RMS to decibels
            return 20 * Math.log10(rms);
        } catch (Exception e) {
            throw new RuntimeException("Error analyzing decibel levels", e);
        }
    }
}
