package com.example.pythatriple_web_api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.pythatriple_web_api.model.PythatripleResult;

public interface PythatripleResultRepository extends JpaRepository<PythatripleResult, Long> {
	Optional<PythatripleResult> findByHypotSq(Integer hypotSq);
}
