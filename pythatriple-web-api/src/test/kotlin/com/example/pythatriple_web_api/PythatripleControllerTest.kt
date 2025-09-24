package com.example.pythatriple_web_api

import com.example.pythatriple_web_api.controller.PythatripleController
import com.example.pythatriple_web_api.dto.PythatripleResponse
import com.example.pythatriple_web_api.dto.PythatripleTableResponse
import com.example.pythatriple_web_api.service.PythatripleService
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.mockito.Mockito.`when` as whenever

@WebMvcTest(PythatripleController::class)
class PythatripleControllerTest {

	@Autowired
	private lateinit var mvc: MockMvc

	@MockitoBean
	private lateinit var service: PythatripleService

	@Test
	@DisplayName("GET /api/triples?hypotenuse_squared=25 returns a valid triple")
	fun testGetTriples_ValidTriple() {
		whenever(service.getTriple(25)).thenReturn(
			PythatripleResponse(3, 4, 5, 4.0)
		)

		mvc.perform(get("/api/triples?hypotenuse_squared=25"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.a").value(3))
			.andExpect(jsonPath("$.b").value(4))
			.andExpect(jsonPath("$.c").value(5))
			.andExpect(jsonPath("$.avg").value(4.0))
	}

	@Test
	@DisplayName("GET /api/triples?hypotenuse_squared=1 returns an empty object")
	fun testGetTriples_EmptyResult() {
		whenever(service.getTriple(1)).thenReturn(null)

		mvc.perform(get("/api/triples?hypotenuse_squared=1"))
			.andExpect(status().isOk())
			.andExpect(content().json("{}"))
	}

	@Test
	@DisplayName("GET /api/triples?hypotenuse_squared=-25 returns a 400 error")
	fun testGetTriples_InvalidParameter() {
		mvc.perform(get("/api/triples?hypotenuse_squared=-25"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.hypotenuse_squared").exists())
	}

	@Test
	@DisplayName("GET /api/triples?hypotenuse_squared= returns a 400 error")
	fun testGetTriples_NoParameter() {
		mvc.perform(get("/api/triples?hypotenuse_squared="))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.hypotenuse_squared").exists())
	}

	@Test
	@DisplayName("GET /api/triples lists all calculated valid triples")
	fun shouldReturnAllTriples() {
		whenever(service.getAllTriples()).thenReturn(listOf(
			PythatripleTableResponse(25, 3, 4, 5, 4.0),
			PythatripleTableResponse(100, 6, 8, 10, 8.0)
		))

		mvc.perform(get("/api/triples"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").isArray())
			.andExpect(jsonPath("$.length()").value(2))
			.andExpect(jsonPath("$[0].hypotSq").value(25))
			.andExpect(jsonPath("$[1].avg").value(8.0))
	}
}
