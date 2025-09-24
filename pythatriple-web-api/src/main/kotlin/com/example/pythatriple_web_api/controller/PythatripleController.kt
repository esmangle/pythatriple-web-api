package com.example.pythatriple_web_api.controller

import com.example.pythatriple_web_api.dto.PythatripleRequest
import com.example.pythatriple_web_api.service.PythatripleService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class PythatripleController(
	private val service: PythatripleService
) {

	@GetMapping("triples")
	fun getAllTriples(): ResponseEntity<*> {
		return ResponseEntity.ok(service.allTriples)
	}

	@GetMapping("triples", params = ["hypotenuse_squared"])
	fun getTriple(
		@Valid @ModelAttribute req: PythatripleRequest,
		br: BindingResult
	): ResponseEntity<*>  {
		if (br.hasErrors()) {
			val errors = br.fieldErrors.associate {
				it.field to it.defaultMessage
			}

			return ResponseEntity.badRequest().body(errors)
		}

		val resp = service.getTriple(req.hypotenuse_squared!!)

		return ResponseEntity.ok().body(
			if (resp.isPresent) resp.get() else emptyMap<Any, Any>()
		)
	}
}
