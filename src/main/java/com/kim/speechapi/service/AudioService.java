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
                private boolean inSilentSegment = false; // Track if we are in a silent segment

                @Override
                public boolean process(AudioEvent audioEvent) {
                    // TODO: Adjust decibel levels to match phone behavior, e.g., set the minimum decibel floor to match what your phone considers silent (e.g., -40 dB).
                    // TODO: Consider normalizing RMS values to a consistent reference level before converting to decibels.
                    double rms = audioEvent.getRMS();
                    double decibels = (rms > 0) ? 20 * Math.log10(rms) : -120; // Assign a minimum decibel value for silence

                    if (decibels < -50) {
                        // Start a new silent segment if we are not already in one
                        if (!inSilentSegment) {
                            metrics.silentSegments++;
                            inSilentSegment = true;
                        }
                    } else {
                        // End the silent segment if the audio is no longer silent
                        inSilentSegment = false;
                    }

                    // TODO: Add a mechanism to handle segments where decibels fluctuate near the silence threshold (-50 dB) to avoid over-counting.
                    metrics.totalDecibels += decibels;
                    metrics.sampleCount++;
                    return true;
                }

                @Override
                public void processingFinished() {
                    // TODO: Validate metrics at the end to ensure accuracy and completeness of analysis.
                }
            });

            dispatcher.run();

            // Calculate the average decibels
            // TODO: Confirm whether silent segments should contribute equally to the average or be weighted differently.
            double averageDecibels = metrics.sampleCount > 0
                    ? metrics.totalDecibels / metrics.sampleCount
                    : -120; // Default to silence if no samples are processed

            // Save to database
            AudioAnalysis analysis = new AudioAnalysis();
            analysis.setFileName(audioFile.getName());
            analysis.setAverageDecibels(averageDecibels);
            analysis.setSilentSegments(metrics.silentSegments);
            repository.save(analysis);

            return analysis;

        } catch (Exception e) {
            e.printStackTrace();
            // TODO: Add better error handling and detailed logging to help diagnose issues in audio processing.
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
        // TODO: Add additional fields if needed, such as the total duration of silent and non-silent segments.
    }
}
