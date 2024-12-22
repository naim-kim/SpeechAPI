package com.kim.speechapi.controller;

import com.kim.speechapi.service.AudioAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Controller
public class AudioAnalysisController {

    @Autowired
    private AudioAnalysisService audioAnalysisService;

    @GetMapping("/")
    public String uploadPage() {
        return "upload"; // Points to upload.html in the templates folder
    }

    @PostMapping("/analyze")
    public String analyzeAudio(@RequestParam("file") MultipartFile file, Model model) throws IOException {
        // Save file to a temporary location
        String filePath = saveToFileSystem(file);

        // Analyze the .wav file
        double decibel = audioAnalysisService.analyzeDecibel(filePath);
        int blanks = audioAnalysisService.countBlanks(filePath);

        // Add results to the model
        model.addAttribute("decibel", decibel);
        model.addAttribute("blanks", blanks);

        return "result"; // Points to result.html in the templates folder
    }

    private String saveToFileSystem(MultipartFile file) throws IOException {
        String filePath = "uploads/" + file.getOriginalFilename();
        File destination = new File(filePath);
        destination.getParentFile().mkdirs();
        file.transferTo(destination);
        return filePath;
    }
}
