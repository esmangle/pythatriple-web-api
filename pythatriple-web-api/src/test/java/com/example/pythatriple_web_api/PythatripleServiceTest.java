package com.example.pythatriple_web_api;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.context.transaction.AfterTransaction;

import jakarta.transaction.Transactional;

import com.example.pythatriple_web_api.dto.PythatripleResponse;
import com.example.pythatriple_web_api.dto.PythatripleTableResponse;
import com.example.pythatriple_web_api.model.PythatripleResult;
import com.example.pythatriple_web_api.repository.PythatripleResultRepository;
import com.example.pythatriple_web_api.service.PythatripleService;

@SpringBootTest
@Transactional
class PythatripleServiceTest {

	@Autowired
	private PythatripleService service;

	@MockitoSpyBean
	private PythatripleResultRepository repository;

	private static void assertValidTriple(
		Optional<PythatripleResponse> res,
		int hypotSq, int a, int b, int c, double avg
	) {
		assertTrue(res.isPresent(), "valid triple isn't present for hypotSq " + hypotSq);
		var r = res.get();
		assertEquals(a, r.a(), "leg A is incorrect for hypotSq " + hypotSq);
		assertEquals(b, r.b(), "leg B is incorrect for hypotSq " + hypotSq);
		assertEquals(c, r.c(), "hypotenuse is incorrect for hypotSq " + hypotSq);
		assertEquals(avg, r.avg(), 0.001, "average is incorrect for hypotSq " + hypotSq);
	}

	@ParameterizedTest(name = "hypotSq {0}: {1}² + {2}² = {3}² (avg={4})")
	@CsvSource({
		"25,3,4,5,4.0", // smallest pythagorean triple
		"169,5,12,13,10.0", // smallest hypotSq not divisible by 5
		"15625,44,117,125,95.333333", // tests for primitive > non-primitive
		"2147395600,27804,37072,46340,37072.0", // largest hypotSq under maxint
	})
	void testValidTriples(
		int hypotSq, int a, int b, int c, double avg
	) {
		doReturn(Optional.empty()).when(repository).findByHypotSq(any());
		doAnswer(i -> i.getArgument(0)).when(repository).save(any());

		assertValidTriple(
			service.getTriples(hypotSq),
			hypotSq, a, b, c, avg
		);
	}

	@ParameterizedTest(name = "hypotSq {0}: no triples, return empty result")
	@CsvSource({"1", "2147483647"})
	void testEmptyResult(int hypotSq) {
		doReturn(Optional.empty()).when(repository).findByHypotSq(any());
		doAnswer(i -> i.getArgument(0)).when(repository).save(any());

		assertTrue(
			service.getTriples(hypotSq).isEmpty(),
			"hypotSq " + hypotSq + " should not have a valid triple"
		);
	}

	@ParameterizedTest(name = "hypotSq {0}: non-positive, throw exception")
	@CsvSource({"0", "-25"})
	void testNonPositive(int hypotSq) {
		assertThrows(
			IllegalArgumentException.class,
			() -> service.getTriples(hypotSq)
		);
	}

	@Test
	@DisplayName("Calculate triples for the same hypotSq only once, and ensure no dupes in database")
	void testCaching() {
		assertValidTriple(
			service.getTriples(25),
			25, 3, 4, 5, 4.0
		);

		verify(repository, times(1))
			.save(any(PythatripleResult.class));

		assertValidTriple(
			service.getTriples(25),
			25, 3, 4, 5, 4.0
		);

		verify(repository, times(1))
			.save(any(PythatripleResult.class));

		service.getTriples(1);

		verify(repository, times(2))
			.save(any(PythatripleResult.class));

		service.getTriples(1);

		verify(repository, times(2))
			.save(any(PythatripleResult.class));
	}

	private static void assertTableRow(
		PythatripleTableResponse row,
		int hypotSq, int a, int b, int c, double avg
	) {
		assertEquals(hypotSq, row.hypotSq());
		assertEquals(a, row.a());
		assertEquals(b, row.b());
		assertEquals(c, row.c());
		assertEquals(avg, row.avg(), 0.001);
	}

	@Test
	@DisplayName("getAllTriples: list calculated triples in reverse insertion order, with only valid triples and no dupes")
	void testGetAllTriples() {
		service.getTriples(25);
		service.getTriples(1);
		service.getTriples(169);
		service.getTriples(169);
		service.getTriples(1);
		service.getTriples(25);

		var list = service.getAllTriples();

		assertEquals(2, list.size(), "more list elements than expected");
		assertTableRow(list.get(0), 169, 5, 12, 13, 10.0);
		assertTableRow(list.get(1), 25, 3, 4, 5, 4.0);

		verify(repository, times(3))
			.save(any(PythatripleResult.class));
	}

	@Test
	@DisplayName("Test all possible input integers to verify completeness and also performance")
	void testTotal() {
		doReturn(Optional.empty()).when(repository).findByHypotSq(any());
		doAnswer(i -> i.getArgument(0)).when(repository).save(any());

		long startTime = System.currentTimeMillis();

		int count = 0;

		for (int c = 1; c <= 46340; c++) {
			var res = service.getTriples(c * c);

			if (res.isPresent()) {
				assertEquals(c, res.get().c());
				count++;
			}
		}

		long endTime = System.currentTimeMillis();

		// this takes about ~2 secs on my i5-12400
		System.out.printf(
			"Found %d valid triples from %d cases in %d ms\n",
			count, 46340, endTime - startTime
		);

		assertEquals(
			count, 31333,
			"there should be exactly 31333 valid triples between 1^2 to 46340^2"
		);
	}

	@AfterEach
	void afterEach() {
		reset(repository);
	}

	@AfterTransaction
	void afterTrans() {
		assertEquals(0L, repository.count());
	}
}
