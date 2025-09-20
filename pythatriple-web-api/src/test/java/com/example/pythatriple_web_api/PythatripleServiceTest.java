package com.example.pythatriple_web_api;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;

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
		int a, int b, int c, double avg
	) {
		assertTrue(res.isPresent());
		var r = res.get();
		assertEquals(a, r.a());
		assertEquals(b, r.b());
		assertEquals(c, r.c());
		assertEquals(avg, r.avg());
	}

	@ParameterizedTest(name = "hypotSq {0}: {1}² + {2}² = {3}² (avg={4})")
	@CsvSource({
		"25,3,4,5,4.0",
		"100,6,8,10,8.0",
		"2147395600,27804,37072,46340,37072.0",
	})
	void testValidTriples(
		int hypotSq, int a, int b, int c, double avg
	) {
		assertValidTriple(
			service.getTriples(hypotSq),
			a, b, c, avg
		);
	}

	@ParameterizedTest(name = "hypotSq {0}: no triples, return empty result")
	@CsvSource({"1", "2147483647"})
	void testEmptyResult(int hypotSq) {
		assertTrue(service.getTriples(hypotSq).isEmpty());
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
			3, 4, 5, 4.0
		);

		verify(repository, times(1))
			.save(any(PythatripleResult.class));

		assertValidTriple(
			service.getTriples(25),
			3, 4, 5, 4.0
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
		assertEquals(avg, row.avg());
	}

	@Test
	@DisplayName("getAllTriples: list calculated triples in reverse insertion order, with only valid triples and no dupes")
	void testGetAllTriples() {
		service.getTriples(25);
		service.getTriples(1);
		service.getTriples(100);
		service.getTriples(100);
		service.getTriples(1);
		service.getTriples(25);

		var table = service.getAllTriples();

		assertEquals(2, table.size());
		assertTableRow(table.get(0), 100, 6, 8, 10, 8.0);
		assertTableRow(table.get(1), 25, 3, 4, 5, 4.0);

		verify(repository, times(3))
			.save(any(PythatripleResult.class));
	}

	@AfterTransaction
	void afterTrans() {
		assertEquals(0L, repository.count());
	}
}
