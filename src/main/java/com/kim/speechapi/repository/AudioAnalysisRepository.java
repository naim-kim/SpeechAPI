package com.kim.speechapi.repository;

import com.kim.speechapi.entity.AudioAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AudioAnalysisRepository extends JpaRepository<AudioAnalysis, Long> {
}
