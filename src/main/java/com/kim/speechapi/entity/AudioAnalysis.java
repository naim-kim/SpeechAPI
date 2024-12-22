package com.kim.speechapi.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;

@Getter
@Entity
public class AudioAnalysis {

    // Getters and setters
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;
    private double averageDecibels;
    private int silentSegments;

    public void setId(Long id) {
        this.id = id;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setAverageDecibels(double averageDecibels) {
        this.averageDecibels = averageDecibels;
    }

    public void setSilentSegments(int silentSegments) {
        this.silentSegments = silentSegments;
    }
}
