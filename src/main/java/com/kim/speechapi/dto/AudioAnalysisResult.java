package com.kim.speechapi.dto;

import lombok.Getter;

@Getter
public class AudioAnalysisResult {

    // Getters and setters
    private double averageDecibels;
    private int silentSegments;

    public void setAverageDecibels(double averageDecibels) {
        this.averageDecibels = averageDecibels;
    }

    public void setSilentSegments(int silentSegments) {
        this.silentSegments = silentSegments;
    }
}
