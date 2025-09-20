package com.example.pythatriple_web_api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PythatripleRequest(
	@NotNull(message = "parameter is required")
	@Positive(message = "must be a positive integer")
	Integer hypotenuse_squared
) {}
