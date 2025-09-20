package com.example.pythatriple_web_api;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.pythatriple_web_api.service.PythatripleService;

@SpringBootTest
class PythatripleServiceTest {

	@Autowired
	private PythatripleService service;

	@Test
	void testValidTriple_25() {
		var res = service.getTriples(25);
		assertTrue(res.isPresent());
		var r = res.get();
		assertEquals(3, r.a());
		assertEquals(4, r.b());
		assertEquals(5, r.c());
		assertEquals(4.0, r.avg());
	}

	@Test
	void testValidTriple_100() {
		var res = service.getTriples(100);
		assertTrue(res.isPresent());
		var r = res.get();
		assertEquals(6, r.a());
		assertEquals(8, r.b());
		assertEquals(10, r.c());
		assertEquals(8.0, r.avg());
	}

	@Test
	void testValidTriple_Large() {
		var res = service.getTriples(2147395600);
		assertTrue(res.isPresent());
		var r = res.get();
		assertEquals(27804, r.a());
		assertEquals(37072, r.b());
		assertEquals(46340, r.c());
		assertEquals(37072.0, r.avg());
	}

	@Test
	void testEmptyResult() {
		assertTrue(service.getTriples(1).isEmpty());
		assertTrue(service.getTriples(Integer.MAX_VALUE).isEmpty());
	}

	@Test
	void testNonPositive() {
		assertThrows(IllegalArgumentException.class, () -> service.getTriples(0));
		assertThrows(IllegalArgumentException.class, () -> service.getTriples(-1));
	}
}
