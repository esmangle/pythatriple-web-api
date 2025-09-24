package com.example.pythatriple_web_api.dto

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive

@JvmRecord
data class PythatripleRequest(
	@field:NotNull(message = "parameter is required")
	@field:Positive(message = "must be a positive integer")
	val hypotenuse_squared: Int?
)
