package com.kim.speechapi.service;

import org.springframework.stereotype.Service;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;

@Service
public class AudioAnalysisService {

    public double analyzeDecibel(String wavFilePath) {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(wavFilePath));
            byte[] buffer = new byte[1024];
            int bytesRead;
            double rms = 0;

            while ((bytesRead = audioInputStream.read(buffer)) != -1) {
                for (int i = 0; i < bytesRead; i++) {
                    rms += buffer[i] * buffer[i];
                }
            }
            rms = Math.sqrt(rms / buffer.length);

            return 20 * Math.log10(rms); // Convert RMS to decibels
        } catch (Exception e) {
            throw new RuntimeException("Error analyzing decibel levels", e);
        }
    }

    public int countBlanks(String wavFilePath) {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(wavFilePath));
            byte[] buffer = new byte[1024];
            int bytesRead;
            int blankCount = 0;

            while ((bytesRead = audioInputStream.read(buffer)) != -1) {
                double rms = 0;
                for (int i = 0; i < bytesRead; i++) {
                    rms += buffer[i] * buffer[i];
                }
                rms = Math.sqrt(rms / buffer.length);

                if (20 * Math.log10(rms) < -60) { // Silence threshold: -60 dB
                    blankCount++;
                }
            }
            return blankCount;
        } catch (Exception e) {
            throw new RuntimeException("Error counting blanks", e);
        }
    }
}
