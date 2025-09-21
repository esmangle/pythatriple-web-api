package com.example.pythatriple_web_api;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.pythatriple_web_api.controller.PythatripleController;
import com.example.pythatriple_web_api.dto.PythatripleResponse;
import com.example.pythatriple_web_api.dto.PythatripleTableResponse;
import com.example.pythatriple_web_api.service.PythatripleService;

@WebMvcTest(PythatripleController.class)
public class PythatripleControllerTest {

	@Autowired
	private MockMvc mvc;

	@MockitoBean
	private PythatripleService service;

	@Test
	@DisplayName("GET /api/triples?hypotenuse_squared=25 returns a valid triple")
	void testGetTriples_ValidTriple() throws Exception {
		when(service.getTriple(25)).thenReturn(
			Optional.of(new PythatripleResponse(3, 4, 5, 4.0))
		);

		mvc.perform(get("/api/triples?hypotenuse_squared=25"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.a").value(3))
			.andExpect(jsonPath("$.b").value(4))
			.andExpect(jsonPath("$.c").value(5))
			.andExpect(jsonPath("$.avg").value(4.0));
	}

	@Test
	@DisplayName("GET /api/triples?hypotenuse_squared=1 returns an empty object")
	void testGetTriples_EmptyResult() throws Exception {
		when(service.getTriple(1)).thenReturn(Optional.empty());

		mvc.perform(get("/api/triples?hypotenuse_squared=1"))
			.andExpect(status().isOk())
			.andExpect(content().json("{}"));
	}

	@Test
	@DisplayName("GET /api/triples?hypotenuse_squared=-25 returns a 400 error")
	void testGetTriples_InvalidParameter() throws Exception {
		mvc.perform(get("/api/triples?hypotenuse_squared=-25"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.hypotenuse_squared").exists());
	}

	@Test
	@DisplayName("GET /api/triples?hypotenuse_squared= returns a 400 error")
	void testGetTriples_NoParameter() throws Exception {
		mvc.perform(get("/api/triples?hypotenuse_squared="))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.hypotenuse_squared").exists());
	}

	@Test
	@DisplayName("GET /api/triples lists all calculated valid triples")
	void shouldReturnAllTriples() throws Exception {
		when(service.getAllTriples()).thenReturn(List.of(
			new PythatripleTableResponse(25, 3, 4, 5, 4.0),
			new PythatripleTableResponse(100, 6, 8, 10, 8.0)
		));

		mvc.perform(get("/api/triples"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").isArray())
			.andExpect(jsonPath("$.length()").value(2))
			.andExpect(jsonPath("$[0].hypotSq").value(25))
			.andExpect(jsonPath("$[1].avg").value(8.0));
	}

}
