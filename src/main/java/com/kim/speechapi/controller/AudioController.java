package com.kim.speechapi.controller;

import com.kim.speechapi.entity.AudioAnalysis;
import com.kim.speechapi.service.AudioService;
import com.kim.speechapi.service.STTService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.web.bind.annotation.RequestParam;

import java.io.File;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/audio")
public class AudioController {

    private final AudioService audioService;
    private final STTService sttService;

    public AudioController(AudioService audioService, STTService sttService) {
        this.audioService = audioService;
        this.sttService = sttService;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadAndAnalyzeAudio(@RequestParam("file") MultipartFile file) {
        try {
            // Save uploaded file to disk
            String uploadDir = "C:/Users/User/GitHub/SpeechAPI/uploads/";
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            File savedFile = new File(uploadDir + file.getOriginalFilename());
            file.transferTo(savedFile);

            // Analyze the saved file and save results in the database
            AudioAnalysis analysis = audioService.analyzeAndSaveAudio(savedFile);

            // Return the analysis results
            return ResponseEntity.ok(analysis);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("An error occurred: " + e.getMessage());
        }
    }

    @PostMapping("/transcribe")
    //TODO: save the transcipt in a new data var
    public ResponseEntity<?> transcribeAudio(@RequestParam("file") MultipartFile file) {
        try {
            // Save the uploaded file temporarily
            String uploadDir = "C:/uploads/";
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            File savedFile = new File(uploadDir + file.getOriginalFilename());
            file.transferTo(savedFile);

            // Transcribe the audio file
            String transcription = sttService.transcribeAudio(savedFile.getAbsolutePath());

            return ResponseEntity.ok(Map.of(
                    "fileName", file.getOriginalFilename(),
                    "transcription", transcription
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("An error occurred: " + e.getMessage());
        }
    }

    // GET endpoint to fetch all audio analysis data
    @GetMapping("/all")
    public ResponseEntity<List<AudioAnalysis>> getAllAudioAnalysis() {
        try {
            List<AudioAnalysis> allAnalysis = audioService.getAllAnalysis();
            return ResponseEntity.ok(allAnalysis);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }


}