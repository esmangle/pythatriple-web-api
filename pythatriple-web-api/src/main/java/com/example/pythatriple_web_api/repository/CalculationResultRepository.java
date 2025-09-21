package com.example.pythatriple_web_api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.pythatriple_web_api.model.CalculationResult;

public interface CalculationResultRepository extends JpaRepository<CalculationResult, Long> {
	Optional<CalculationResult> findByHypotSq(Integer hypotSq);
	List<CalculationResult> findAllByOrderByTimestampDesc();
}
