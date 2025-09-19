package com.example.pythatriple_web_api.service;

import org.springframework.stereotype.Service;

import com.example.pythatriple_web_api.dto.PythatripleResponse;

@Service
public class PythatripleService {
	public PythatripleResponse getTriples(int hypotSq) {
		return calculateTriples(hypotSq);
	}

	private PythatripleResponse calculateTriples(int hypotSq) {
		double cDouble = Math.sqrt(hypotSq);

		int c = (int) Math.round(cDouble);

		if (c * c != hypotSq) {
			return null;
		}

		int aMax = (int) (c / Math.sqrt(2));

		for (int a = 1; a <= aMax; a++) {
			int bSq = hypotSq - (a * a);

			if (bSq <= 0) {
				continue;
			}

			double bDouble = Math.sqrt(bSq);

			int b = (int) Math.round(bDouble);

			if (b * b != bSq) {
				continue;
			}

			if (a >= b) {
				continue;
			}

			double avg = (a + b + c) / 3.0;

			return new PythatripleResponse(
				a, b, c, avg
			);
		}

		return null;
	}
}
