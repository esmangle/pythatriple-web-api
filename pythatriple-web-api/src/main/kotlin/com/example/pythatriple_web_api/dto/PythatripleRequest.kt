package com.example.pythatriple_web_api.dto

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive

data class PythatripleRequest(
	@field:NotNull(message = "parameter is required")
	@field:Positive(message = "must be a positive integer")
	private val hypotenuse_squared: Int?
){
	val hypotSq: Int get() = requireNotNull(hypotenuse_squared)
}
