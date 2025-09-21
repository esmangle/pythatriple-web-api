package com.example.pythatriple_web_api.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;

@Entity
@Table(name = "calculation_results", uniqueConstraints = {
	@UniqueConstraint(columnNames = "hypotenuse_squared", name = "uq_hypotsq")
})
public class CalculationResult {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(
		name = "hypotenuse_squared", nullable = false, unique = true,
		columnDefinition = "INT UNSIGNED"
	)
	private Integer hypotSq;

	@ManyToOne
	@JoinColumn(
		name = "triple_id", nullable = true,
		foreignKey = @ForeignKey(name = "fk_triple")
	)
	private TripleResult triple = null;

	@CreationTimestamp
	@Column(
		name = "created_at", nullable = false, updatable = false,
		columnDefinition = "TIMESTAMP"
	)
	private LocalDateTime timestamp;

	protected CalculationResult() {}

	public CalculationResult(Integer hypotSq) {
		this.hypotSq = hypotSq;
	}

	public CalculationResult(Integer hypotSq, TripleResult triple) {
		this.hypotSq = hypotSq;
		this.triple = triple;
	}

	public Long getId() { return id; }
	public Integer getHypotSq() { return hypotSq; }
	public TripleResult getTriple() { return triple; }

	public boolean isValid() {
		return triple != null;
	}

	public boolean isEmpty() {
		return !isValid();
	}
}
