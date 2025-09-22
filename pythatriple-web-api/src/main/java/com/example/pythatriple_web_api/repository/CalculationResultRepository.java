package com.example.pythatriple_web_api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.pythatriple_web_api.model.CalculationResult;

@Repository
public interface CalculationResultRepository extends JpaRepository<CalculationResult, Long> {
	@EntityGraph(attributePaths = {"triple"})
	Optional<CalculationResult> findByHypotSq(Integer hypotSq);
	@EntityGraph(attributePaths = {"triple"})
	List<CalculationResult> findAllByOrderByTimestampDesc();
}
