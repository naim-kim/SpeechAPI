package com.kim.speechapi.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@RestController
@RequestMapping("/api/audio")
public class AudioController {

    @PostMapping("/upload")
    public ResponseEntity<?> uploadAudio(@RequestParam("file") MultipartFile file) {
        try {
            // Check if the file is empty
            if (file.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File is empty!");
            }

            // Log file details
            System.out.println("File received: " + file.getOriginalFilename());
            System.out.println("File size: " + file.getSize() + " bytes");

            // Save the file to a specific location
            String uploadDir = "C:/Users/User/GitHub/SpeechAPI/uploads/"; // Change to your desired directory
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs(); // Create the directory if it doesn't exist
            }

            File savedFile = new File(uploadDir + file.getOriginalFilename());
            file.transferTo(savedFile);

            System.out.println("File saved to: " + savedFile.getAbsolutePath());

            return ResponseEntity.ok("File uploaded and saved successfully: " + savedFile.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while uploading the file.");
        }
    }

}
