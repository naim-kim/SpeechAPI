package com.kim.speechapi.service;

import com.kim.speechapi.dto.AudioAnalysisResult;
import com.kim.speechapi.entity.AudioAnalysis;
import com.kim.speechapi.repository.AudioAnalysisRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.jvm.AudioDispatcherFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

@Service
public class AudioService {

    private final AudioAnalysisRepository repository;

    public AudioService(AudioAnalysisRepository repository) {
        this.repository = repository;
    }

    public AudioAnalysisResult analyzeAudio(MultipartFile file) throws Exception {
        File tempFile = null;
        try {
            // Save the uploaded file as a temporary file
            tempFile = File.createTempFile("uploaded", ".wav");
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                fos.write(file.getBytes());
            }

            // Use TarsosDSP to process the temporary file
            AudioDispatcher dispatcher = AudioDispatcherFactory.fromPipe(
                    tempFile.getAbsolutePath(), 44100, 1024, 512);

            final class AnalysisMetrics {
                double totalDecibels = 0;
                int sampleCount = 0;
                int silentSegments = 0;
            }
            AnalysisMetrics metrics = new AnalysisMetrics();

            dispatcher.addAudioProcessor(new AudioProcessor() {
                @Override
                public boolean process(AudioEvent audioEvent) {
                    double rms = audioEvent.getRMS();
                    double decibels = 20 * Math.log10(rms);

                    if (decibels < -50) {
                        metrics.silentSegments++;
                    }

                    metrics.totalDecibels += decibels;
                    metrics.sampleCount++;
                    return true;
                }

                @Override
                public void processingFinished() {
                }
            });

            dispatcher.run();

            double averageDecibels = metrics.totalDecibels / metrics.sampleCount;

            // Save to database
            AudioAnalysis entity = new AudioAnalysis();
            entity.setFileName(file.getOriginalFilename());
            entity.setAverageDecibels(averageDecibels);
            entity.setSilentSegments(metrics.silentSegments);
            repository.save(entity);

            AudioAnalysisResult result = new AudioAnalysisResult();
            result.setAverageDecibels(averageDecibels);
            result.setSilentSegments(metrics.silentSegments);
            return result;

        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error occurred while processing audio file: " + e.getMessage());
        } finally {
            // Clean up temporary file
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
    }
}