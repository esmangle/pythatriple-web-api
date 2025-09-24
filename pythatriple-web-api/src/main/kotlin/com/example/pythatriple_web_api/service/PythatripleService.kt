package com.example.pythatriple_web_api.service

import com.example.pythatriple_web_api.dto.PythatripleResponse
import com.example.pythatriple_web_api.dto.PythatripleTableResponse
import com.example.pythatriple_web_api.model.CalculationResult
import com.example.pythatriple_web_api.model.TripleResult
import com.example.pythatriple_web_api.repository.CalculationResultRepository
import com.example.pythatriple_web_api.repository.TripleResultRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PythatripleService(
	private val calcRepo: CalculationResultRepository,
	private val tripleRepo: TripleResultRepository
) {

	@Transactional(readOnly = true)
	fun getAllTriples(): List<PythatripleTableResponse> {
		return calcRepo.findAllByOrderByTimestampDesc()
			.filter { it.isValid }
			.map { c ->
				val t = c.triple!!
				PythatripleTableResponse(
					c.hypotSq, t.a, t.b, t.c, t.avg
				)
			}
	}

	@Transactional
	fun getTriple(hypotSq: Int): PythatripleResponse? {
		if (hypotSq <= 0) {
			throw IllegalArgumentException(
				"hypotSq must be a positive integer, but was: $hypotSq"
			)
		}

		val cached = calcRepo.findByHypotSq(hypotSq)

		if (cached != null) {
			if (cached.isEmpty) {
				return null
			}

			val triple = cached.triple!!

			return PythatripleResponse(
				triple.a, triple.b, triple.c, triple.avg
			)
		}

		val r = calculateTriple(hypotSq)

		val calc = if (r == null) {
			CalculationResult(hypotSq)
		} else {
			val triple = tripleRepo.findByAAndBAndC(r.a, r.b, r. c)
				?: tripleRepo.save(
					TripleResult(r.a, r.b, r.c, r.avg)
				)

			CalculationResult(hypotSq, triple)
		}

		calcRepo.save(calc)

		return r
	}

	private fun calculateTriple(hypotSq: Int): PythatripleResponse? {
		return null
	}
}
