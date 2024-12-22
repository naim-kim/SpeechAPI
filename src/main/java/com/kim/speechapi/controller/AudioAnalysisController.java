package com.kim.speechapi.controller;

import com.kim.speechapi.security.AudioAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/api/audio")
public class AudioAnalysisController {

    @Autowired
    private AudioAnalysisService audioAnalysisService;

    @PostMapping("/analyze-decibel")
    public double analyzeDecibel(@RequestParam("file") MultipartFile file) throws IOException {
        // Save the file temporarily
        String filePath = saveToFileSystem(file);
        return audioAnalysisService.analyzeDecibel(filePath);
    }

    private String saveToFileSystem(MultipartFile file) throws IOException {
        String filePath = "uploads/" + file.getOriginalFilename();
        file.transferTo(new File(filePath));
        return filePath;
    }
}
