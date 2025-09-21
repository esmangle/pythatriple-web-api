package com.example.pythatriple_web_api.model;

import jakarta.persistence.*;

@Entity
@Table(name = "triple_results", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"leg_a", "leg_b", "hypotenuse"}, name = "uq_triple")
})
public class TripleResult {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(
		name = "leg_a", nullable = false,
		columnDefinition = "INT UNSIGNED"
	)
	private Integer a;

	@Column(
		name = "leg_b", nullable = false,
		columnDefinition = "INT UNSIGNED"
	)
	private Integer b;

	@Column(
		name = "hypotenuse", nullable = false,
		columnDefinition = "INT UNSIGNED"
	)
	private Integer c;

	@Column(name = "average", nullable = false)
	private Double avg;


	protected TripleResult() {}

	public TripleResult(
		int a, int b, int c, double avg
	) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.avg = avg;
	}

	public Long getId() { return id; }
	public Integer getA() { return a; }
	public Integer getB() { return b; }
	public Integer getC() { return c; }
	public Double getAvg() { return avg; }
}
