package com.example.pythatriple_web_api.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.constraints.Positive;

import com.example.pythatriple_web_api.dto.PythatripleResponse;
import com.example.pythatriple_web_api.service.PythatripleService;


@RestController
@RequestMapping("/api")
public class PythatripleController {

	@Autowired
	private PythatripleService service;

	@GetMapping("triples")
	public ResponseEntity<?> getTriples(
		@RequestParam(name = "hypotenuse_squared") @Positive int hypotSq
	) {
		PythatripleResponse result = service.getTriples(hypotSq);

		return ResponseEntity.ok().body(
			(result == null) ? Map.of() : result
		);
	}
}
