package com.example.pythatriple_web_api.service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.pythatriple_web_api.dto.PythatripleResponse;
import com.example.pythatriple_web_api.model.PythatripleResult;
import com.example.pythatriple_web_api.repository.PythatripleResultRepository;

@Service
public class PythatripleService {
	@Autowired
	private PythatripleResultRepository repository;

	public List<PythatripleResponse> getAllTriples() {
		return repository.findAllByOrderByTimestampDesc()
			.stream()
			.filter(PythatripleResult::isValid)
			.map(result -> new PythatripleResponse(
				result.getA(), result.getB(), result.getC(), result.getAvg()
			))
			.collect(Collectors.toList());
	}

	public Optional<PythatripleResponse> getTriples(int hypotSq) {
		if (hypotSq <= 0) {
			throw new IllegalArgumentException(
				"hypotSq must be a positive integer, but was: " + hypotSq
			);
		}

		var cached = repository.findByHypotSq(hypotSq);

		if (cached.isPresent()) {
			var result = cached.get();

			if (result.isEmpty()) {
				return Optional.empty();
			}

			return Optional.of(new PythatripleResponse(
				result.getA(), result.getB(), result.getC(), result.getAvg()
			));
		}

		var resp = calculateTriples(hypotSq);

		var result = resp == null
			? new PythatripleResult(hypotSq)
			: new PythatripleResult(
				hypotSq, resp.a(), resp.b(), resp.c(), resp.avg()
			);

		repository.save(result);

		return Optional.ofNullable(resp);
	}

	private PythatripleResponse calculateTriples(int hypotSq) {
		assert hypotSq > 0 : "hypotSq must be a positive integer";

		double cDouble = Math.sqrt(hypotSq);

		int c = (int) Math.round(cDouble);

		if (c * c != hypotSq) {
			return null;
		}

		record Triple(
			int a, int b, int c, double avg, boolean primitive
		) {}

		Comparator<Triple> comp = Comparator
			.comparing(Triple::primitive, Comparator.reverseOrder())
			.thenComparingDouble(Triple::avg);

		Triple best = null;

		int aMax = (int) (c / Math.sqrt(2));

		for (int a = 1; a <= aMax; a++) {
			int bSq = hypotSq - (a * a);

			if (bSq <= 0) continue;

			double bDouble = Math.sqrt(bSq);

			int b = (int) Math.round(bDouble);

			if (b * b != bSq) continue;

			if (a >= b) continue;

			double avg = (a + b + c) / 3.0;

			boolean primitive = gcd(a, gcd(b, c)) == 1;

			Triple triple = new Triple(a, b, c, avg, primitive);

			if (best == null || comp.compare(best, triple) < 0) {
				best = triple;
			}
		}

		if (best == null) {
			return null;
		}

		return new PythatripleResponse(
			best.a(), best.b(), best.c(), best.avg()
		);
	}

	private static int gcd(int a, int b) {
		while (b != 0) {
			int temp = b;
			b = a % b;
			a = temp;
		}
		return a;
	}
}
