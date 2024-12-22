package com.kim.speechapi.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "audio_analysis")
public class AudioAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment ID
    private Long id;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "average_decibels", nullable = false)
    private Double averageDecibels;

    @Column(name = "silent_segments", nullable = false)
    private Integer silentSegments;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Double getAverageDecibels() {
        return averageDecibels;
    }

    public void setAverageDecibels(Double averageDecibels) {
        this.averageDecibels = averageDecibels;
    }

    public Integer getSilentSegments() {
        return silentSegments;
    }

    public void setSilentSegments(Integer silentSegments) {
        this.silentSegments = silentSegments;
    }
}
