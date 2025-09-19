package com.example.pythatriple_web_api.service;

import org.springframework.stereotype.Service;

import com.example.pythatriple_web_api.dto.PythatripleResponse;

@Service
public class PythatripleService {
	public PythatripleResponse getTriples(int hypotSq) {
		return calculateTriples(hypotSq);
	}

	private PythatripleResponse calculateTriples(int hypotSq) {
		int c = (int) Math.sqrt(hypotSq);

		for (int a = 1; a < c; a++) {
			int bSq = hypotSq - (a * a);

			double bDouble = Math.sqrt(bSq);

			if (bDouble % 1.0 == 0.0) {
				int b = (int) bDouble;

				double avg = (a + b + c) / 3.0;

				return new PythatripleResponse(
					a, b, c, avg
				);
			}
		}

		return null;
	}
}
