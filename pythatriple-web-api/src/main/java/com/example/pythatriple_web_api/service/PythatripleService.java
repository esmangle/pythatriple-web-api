package com.example.pythatriple_web_api.service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.pythatriple_web_api.dto.PythatripleResponse;
import com.example.pythatriple_web_api.dto.PythatripleTableResponse;
import com.example.pythatriple_web_api.model.CalculationResult;
import com.example.pythatriple_web_api.model.TripleResult;
import com.example.pythatriple_web_api.repository.CalculationResultRepository;
import com.example.pythatriple_web_api.repository.TripleResultRepository;

@Service
public class PythatripleService {

	@Autowired
	private CalculationResultRepository calcRepo;

	@Autowired
	private TripleResultRepository tripleRepo;

	public List<PythatripleTableResponse> getAllTriples() {
		return calcRepo.findAllByOrderByTimestampDesc()
			.stream()
			.filter(CalculationResult::isValid)
			.map(c -> {
				var t = c.getTriple();
				return new PythatripleTableResponse(
					c.getHypotSq(), t.getA(), t.getB(), t.getC(), t.getAvg()
				);
			})
			.collect(Collectors.toList());
	}

	public Optional<PythatripleResponse> getTriple(int hypotSq) {
		if (hypotSq <= 0) {
			throw new IllegalArgumentException(
				"hypotSq must be a positive integer, but was: " + hypotSq
			);
		}

		var cached = calcRepo.findByHypotSq(hypotSq);

		if (cached.isPresent()) {
			var calc = cached.get();

			if (calc.isEmpty()) {
				return Optional.empty();
			}

			var triple = calc.getTriple();

			return Optional.of(new PythatripleResponse(
				triple.getA(), triple.getB(), triple.getC(), triple.getAvg()
			));
		}

		var r = calculateTriple(hypotSq);

		CalculationResult calc;

		if (r == null) {
			calc = new CalculationResult(hypotSq);
		} else {
			var triple = tripleRepo.findByAAndBAndC(r.a(), r.b(), r.c())
				.orElseGet(() -> tripleRepo.save(
					new TripleResult(r.a(), r.b(), r.c(), r.avg())
				));

			calc = new CalculationResult(hypotSq, triple);
		}

		calcRepo.save(calc);

		return Optional.ofNullable(r);
	}

	private PythatripleResponse calculateTriple(int hypotSq) {
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
			.comparing(Triple::primitive)
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
