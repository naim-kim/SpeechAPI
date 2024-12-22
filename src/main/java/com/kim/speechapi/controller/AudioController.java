package com.kim.speechapi.controller;

import com.kim.speechapi.entity.AudioAnalysis;
import com.kim.speechapi.service.AudioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@RestController
@RequestMapping("/api/audio")
public class AudioController {

    private final AudioService audioService;

    public AudioController(AudioService audioService) {
        this.audioService = audioService;
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


}