package com.example.pythatriple_web_api.repository

import com.example.pythatriple_web_api.model.TripleResult
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TripleResultRepository : JpaRepository<TripleResult, Long> {
	fun findByAAndBAndC(a: Int, b: Int, c: Int): TripleResult?
}
