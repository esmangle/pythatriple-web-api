package com.example.pythatriple_web_api.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api")
public class PythatripleController {

	@GetMapping("triples")
	public ResponseEntity<?> getTriples(@RequestParam Integer value) {
		return ResponseEntity.ok().body(Map.of("test", "message"));
	}
}
