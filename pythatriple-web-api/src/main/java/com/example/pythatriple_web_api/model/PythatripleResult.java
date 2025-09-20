package com.example.pythatriple_web_api.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "triple_results")
public class PythatripleResult {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(
		name = "hypotenuse_squared", nullable = false, unique = true,
		columnDefinition = "BIGINT UNSIGNED"
	)
	private Integer hypotSq;

	@Column(
		name = "leg_a", nullable = true,
		columnDefinition = "INT UNSIGNED"
	)
	private Integer a;

	@Column(
		name = "leg_b", nullable = true,
		columnDefinition = "INT UNSIGNED"
	)
	private Integer b;

	@Column(
		name = "hypotenuse", nullable = true,
		columnDefinition = "INT UNSIGNED"
	)
	private Integer c;

	@Column(name = "average", nullable = true)
	private Double avg;

	@CreationTimestamp
	@Column(
		name = "created_at", nullable = false, updatable = false,
		columnDefinition = "TIMESTAMP"
	)
	private LocalDateTime timestamp;

	protected PythatripleResult() {}

	public PythatripleResult(
		int hypotSq, int a, int b, int c, double avg
	) {
		this.hypotSq = hypotSq;
		this.a = a;
		this.b = b;
		this.c = c;
		this.avg = avg;
	}

	public PythatripleResult(int hypotSq) {
		this.hypotSq = hypotSq;
	}

	public Long getId() { return id; }
	public Integer getHypotSq() { return hypotSq; }
	public Integer getA() { return a; }
	public Integer getB() { return b; }
	public Integer getC() { return c; }
	public Double getAvg() { return avg; }
	public LocalDateTime getTimestamp() { return timestamp; }
}
