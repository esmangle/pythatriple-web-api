package com.example.pythatriple_web_api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.pythatriple_web_api.model.TripleResult;

@Repository
public interface TripleResultRepository extends JpaRepository<TripleResult, Long> {
	Optional<TripleResult> findByAAndBAndC(Integer a, Integer b, Integer c);
}
