package com.example.pythatriple_web_api

import com.example.pythatriple_web_api.dto.PythatripleResponse
import com.example.pythatriple_web_api.dto.PythatripleTableResponse
import com.example.pythatriple_web_api.repository.CalculationResultRepository
import com.example.pythatriple_web_api.repository.TripleResultRepository
import com.example.pythatriple_web_api.service.PythatripleService
import org.junit.jupiter.api.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean
import org.springframework.test.context.transaction.AfterTransaction
import org.springframework.transaction.annotation.Transactional
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@SpringBootTest
@Transactional
class PythatripleServiceTest {

	@Autowired
	private lateinit var service: PythatripleService

	@MockitoSpyBean
	private lateinit var calcRepo: CalculationResultRepository

	@MockitoSpyBean
	private lateinit var tripleRepo: TripleResultRepository

	private fun assertValidTriple(
		res: PythatripleResponse?,
		hypotSq: Int, a: Int, b: Int, c: Int, avg: Double
	) {
		assertNotNull(res, "valid triple isn't present for hypotSq $hypotSq")
		res!!
		assertEquals(a, res.a, "leg A is incorrect for hypotSq $hypotSq")
		assertEquals(b, res.b, "leg B is incorrect for hypotSq $hypotSq")
		assertEquals(c, res.c, "hypotenuse is incorrect for hypotSq $hypotSq")
		assertEquals(avg, res.avg, 0.001, "average is incorrect for hypotSq $hypotSq")
	}

	private fun assertTableRow(
		row: PythatripleTableResponse,
		hypotSq: Int, a: Int, b: Int, c: Int, avg: Double
	) {
		assertEquals(hypotSq, row.hypotSq)
		assertEquals(a, row.a)
		assertEquals(b, row.b)
		assertEquals(c, row.c)
		assertEquals(avg, row.avg, 0.001)
	}

	private fun stubRepositories() {
		doReturn(null).`when`(calcRepo).findByHypotSq(anyInt())
		doAnswer { it.getArgument(0) }.`when`(calcRepo).save(any())
		doReturn(null).`when`(tripleRepo).findByAAndBAndC(anyInt(), anyInt(), anyInt())
		doAnswer { it.getArgument(0) }.`when`(tripleRepo).save(any())
	}

	@ParameterizedTest(name = "hypotSq {0}: {1}² + {2}² = {3}² (avg={4})")
	@CsvSource(
		"25,3,4,5,4.0", // smallest pythagorean triple
		"169,5,12,13,10.0", // smallest hypotSq not divisible by 5
		"15625,44,117,125,95.333333", // tests for primitive > non-primitive
		"2147395600,27804,37072,46340,37072.0" // largest hypotSq under maxint
	)
	fun testValidTriples(hypotSq: Int, a: Int, b: Int, c: Int, avg: Double) {
		stubRepositories()

		val result = service.getTriple(hypotSq)
		assertValidTriple(result, hypotSq, a, b, c, avg)
	}

	@ParameterizedTest(name = "hypotSq {0}: no triples, return empty result")
	@CsvSource("1", "2147483647")
	fun testEmptyResult(hypotSq: Int) {
		stubRepositories()

		val result = service.getTriple(hypotSq)
		assertNull(result, "hypotSq $hypotSq should not have a valid triple")
	}

	@ParameterizedTest(name = "hypotSq {0}: non-positive, throw exception")
	@CsvSource("0", "-25")
	fun testNonPositive(hypotSq: Int) {
		//stubRepositories()

		assertThrows<IllegalArgumentException> {
			service.getTriple(hypotSq)
		}
	}

	@Test
	@DisplayName("Calculate triples for the same hypotSq only once, and ensure no dupes in database")
	fun testCaching() {
		assertValidTriple(service.getTriple(25), 25, 3, 4, 5, 4.0)

		verify(calcRepo, times(1)).save(any())
		verify(tripleRepo, times(1)).save(any())

		assertValidTriple(service.getTriple(25), 25, 3, 4, 5, 4.0)

		verify(calcRepo, times(1)).save(any())
		verify(tripleRepo, times(1)).save(any())

		service.getTriple(1)

		verify(calcRepo, times(2)).save(any())
		verify(tripleRepo, times(1)).save(any())

		service.getTriple(1)

		verify(calcRepo, times(2)).save(any())
		verify(tripleRepo, times(1)).save(any())
	}

	@Test
	@DisplayName("getAllTriples: list calculated triples in reverse insertion order, with only valid triples and no dupes")
	fun testGetAllTriples() {
		service.getTriple(25)
		service.getTriple(1)
		service.getTriple(169)
		service.getTriple(169)
		service.getTriple(1)
		service.getTriple(25)

		val list = service.getAllTriples()

		assertEquals(2, list.size, "more list elements than expected")
		assertTableRow(list[0], 169, 5, 12, 13, 10.0)
		assertTableRow(list[1], 25, 3, 4, 5, 4.0)

		verify(calcRepo, times(3)).save(any())
		verify(tripleRepo, times(2)).save(any())
	}

	@Test
	@DisplayName("Test all possible input integers to verify completeness and also performance")
	fun testTotal() {
		stubRepositories()

		val startTime = System.currentTimeMillis()

		var count = 0

		for (c in 1 .. 46340) {
			val res = service.getTriple(c * c)

			if (res != null) {
				assertEquals(c, res.c)
				count++
			}
		}

		val endTime = System.currentTimeMillis()

		// this takes about ~2 secs on my i5-12400
		println("Found $count valid triples from 46340 cases in ${endTime - startTime} ms")

		assertEquals(31333, count, "there should be exactly 31333 valid triples between 1^2 to 46340^2")
	}

	@AfterEach
	fun afterEach() {
		reset(calcRepo)
		reset(tripleRepo)
	}

	@AfterTransaction
	fun afterTrans() {
		assertEquals(0L, calcRepo.count())
		assertEquals(0L, tripleRepo.count())
	}
}
