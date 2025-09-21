package com.example.pythatriple_web_api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.pythatriple_web_api.model.TripleResult;

public interface TripleResultRepository extends JpaRepository<TripleResult, Long> {
	Optional<TripleResult> findByAAndBAndC(Integer a, Integer b, Integer c);
}
