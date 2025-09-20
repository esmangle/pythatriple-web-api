package com.example.pythatriple_web_api.controller;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import com.example.pythatriple_web_api.dto.PythatripleRequest;
import com.example.pythatriple_web_api.service.PythatripleService;


@RestController
@RequestMapping("/api")
public class PythatripleController {

	@Autowired
	private PythatripleService service;

	@GetMapping("triples")
	public ResponseEntity<?> getTriples(
		@Valid @ModelAttribute PythatripleRequest req,
		BindingResult br
	) {
		if (br.hasErrors()) {
			var errors = br.getFieldErrors()
				.stream()
				.collect(Collectors.toMap(
					FieldError::getField,
					FieldError::getDefaultMessage
				));

			return ResponseEntity.badRequest().body(errors);
		}

		var resp = service.getTriples(req.hypotenuse_squared());

		return ResponseEntity.ok().body(
			resp.isPresent() ? resp.get() : Map.of()
		);
	}
}
