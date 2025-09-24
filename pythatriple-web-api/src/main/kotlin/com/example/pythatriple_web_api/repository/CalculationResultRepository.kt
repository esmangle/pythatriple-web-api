package com.example.pythatriple_web_api.repository

import com.example.pythatriple_web_api.model.CalculationResult
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CalculationResultRepository : JpaRepository<CalculationResult, Long> {
	@EntityGraph(attributePaths = ["triple"])
	fun findByHypotSq(hypotSq: Int): CalculationResult?

	@EntityGraph(attributePaths = ["triple"])
	fun findAllByOrderByTimestampDesc(): List<CalculationResult>
}
