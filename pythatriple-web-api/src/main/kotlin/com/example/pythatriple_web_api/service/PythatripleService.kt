package com.example.pythatriple_web_api.service

import com.example.pythatriple_web_api.dto.PythatripleResponse
import com.example.pythatriple_web_api.dto.PythatripleTableResponse
import com.example.pythatriple_web_api.model.CalculationResult
import com.example.pythatriple_web_api.model.TripleResult
import com.example.pythatriple_web_api.repository.CalculationResultRepository
import com.example.pythatriple_web_api.repository.TripleResultRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.math.roundToInt
import kotlin.math.sqrt

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
		require(hypotSq > 0) {
			"hypotSq must be a positive integer, but was: $hypotSq"
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
		require(hypotSq > 0) {
			"hypotSq must be a positive integer, but was: $hypotSq"
		}

		val cDouble = sqrt(hypotSq.toDouble())
		val c = cDouble.roundToInt()

		if (c * c != hypotSq) {
			return null
		}

		data class Triple(
			val a: Int, val b: Int, val c: Int,
			val avg: Double, val primitive: Boolean
		)

		val comp = compareBy<Triple> { it.primitive }.thenBy { it.avg }

		var best: Triple? = null

		for (a in 1..(c / sqrt(2.0)).toInt()) {
			val bSq = hypotSq - (a * a)

			if (bSq <= 0) continue

			val bDouble = sqrt(bSq.toDouble())

			val b = bDouble.roundToInt()

			if (b * b != bSq) continue

			if (a >= b) continue

			val avg = (a + b + c) / 3.0

			val primitive = gcd(a, gcd(b, c)) == 1

			val triple = Triple(a, b, c, avg, primitive)

			if (best == null || comp.compare(best, triple) < 0) {
				best = triple
			}
		}

		return best?.let {
			PythatripleResponse(it.a, it.b, it.c, it.avg)
		}
	}

	private fun gcd(a: Int, b: Int): Int {
		var x = a
		var y = b
		while (y != 0) {
			val temp = y
			y = x % y
			x = temp
		}
		return x
	}
}
