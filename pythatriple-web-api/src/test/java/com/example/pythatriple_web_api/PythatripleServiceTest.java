package com.example.pythatriple_web_api;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
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

	@Test
	void testValidTriple_25() {
		assertValidTriple(
			service.getTriples(25),
			3, 4, 5, 4.0
		);
	}

	@Test
	void testValidTriple_100() {
		assertValidTriple(
			service.getTriples(100),
			6, 8, 10, 8.0
		);
	}

	@Test
	void testValidTriple_Large() {
		assertValidTriple(
			service.getTriples(2147395600),
			27804, 37072, 46340, 37072.0
		);
	}

	@Test
	void testEmptyResult() {
		assertTrue(service.getTriples(1).isEmpty());
		assertTrue(service.getTriples(2147483647).isEmpty());
	}

	@Test
	void testNonPositive() {
		assertThrows(IllegalArgumentException.class, () -> service.getTriples(0));
		assertThrows(IllegalArgumentException.class, () -> service.getTriples(-1));
	}

	@Test
	void testCaching() {
		assertValidTriple(
			service.getTriples(25),
			3, 4, 5, 4.0
		);
		assertValidTriple(
			service.getTriples(25),
			3, 4, 5, 4.0
		);

		verify(repository, times(1))
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
