package com.kim.speechapi;

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
public class HomeController {

    @Autowired
    private AudioAnalysisService audioAnalysisService;

    // Render the upload page
    @GetMapping("/upload")
    public String home() {
        return "upload"; // Points to upload.html in templates
    }

    // Handle file upload and analysis
    @PostMapping("/analyze")
    public String analyzeAudio(@RequestParam("file") MultipartFile file, Model model) throws IOException {
        // Save the file to a temporary directory
        String filePath = saveToFileSystem(file);

        // Perform audio analysis
        double decibel = audioAnalysisService.analyzeDecibel(filePath);
        int blanks = audioAnalysisService.countBlanks(filePath);

        // Add analysis results to the model for display
        model.addAttribute("decibel", decibel);
        model.addAttribute("blanks", blanks);

        return "result"; // Points to result.html in templates
    }

    // Helper method to save the uploaded file
    private String saveToFileSystem(MultipartFile file) throws IOException {
        String filePath = "uploads/" + file.getOriginalFilename();
        File destination = new File(filePath);
        destination.getParentFile().mkdirs(); // Ensure the directory exists
        file.transferTo(destination); // Save the file
        return filePath;
    }
}
