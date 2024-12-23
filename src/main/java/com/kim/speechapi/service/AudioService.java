package com.kim.speechapi.service;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.jvm.AudioDispatcherFactory;
import com.kim.speechapi.entity.AudioAnalysis;
import com.kim.speechapi.repository.AudioAnalysisRepository;
import jakarta.persistence.Table;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
public class AudioService {

    private final AudioAnalysisRepository repository;

    public AudioService(AudioAnalysisRepository repository) {
        this.repository = repository;
    }

    public AudioAnalysis analyzeAndSaveAudio(File audioFile) {
        try {
            AudioDispatcher dispatcher = AudioDispatcherFactory.fromPipe(
                    audioFile.getAbsolutePath(), 44100, 1024, 512);

            AnalysisMetrics metrics = new AnalysisMetrics();

            dispatcher.addAudioProcessor(new AudioProcessor() {
                @Override
                public boolean process(AudioEvent audioEvent) {
                    double rms = audioEvent.getRMS();
                    double decibels = (rms > 0) ? 20 * Math.log10(rms) : -120; // Assign a minimum decibel value for silence

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

            // Calculate the average decibels
            double averageDecibels = metrics.sampleCount > 0
                    ? metrics.totalDecibels / metrics.sampleCount
                    : -120; // Default to silence if no samples are processed

            // Save to database
            AudioAnalysis analysis = new AudioAnalysis();
            analysis.setFileName(audioFile.getName());
            analysis.setAverageDecibels(averageDecibels);
            analysis.setSilentSegments(metrics.silentSegments);
            repository.save(analysis); // This saves it to the database

            return analysis;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error analyzing audio file: " + e.getMessage());
        }


    }

    // fetch all records
    public List<AudioAnalysis> getAllAnalysis() {
        return repository.findAll();
    }


    // Wrapper class to hold analysis metrics
    private static class AnalysisMetrics {
        double totalDecibels = 0;
        int sampleCount = 0;
        int silentSegments = 0;
    }
}