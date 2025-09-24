package com.example.pythatriple_web_api.model

import jakarta.persistence.*
import org.hibernate.annotations.NaturalId

@Entity
@Table(
	name = "triple_results",
	uniqueConstraints = [
		UniqueConstraint(columnNames = ["leg_a", "leg_b", "hypotenuse"], name = "uq_triple")
	]
)
class TripleResult(
	@NaturalId
	@Column(
		name = "leg_a", nullable = false,
		columnDefinition = "INT UNSIGNED"
	)
	val a: Int,

	@NaturalId
	@Column(
		name = "leg_b", nullable = false,
		columnDefinition = "INT UNSIGNED"
	)
	val b: Int,

	@NaturalId
	@Column(
		name = "hypotenuse", nullable = false,
		columnDefinition = "INT UNSIGNED"
	)
	val c: Int,

	@Column(name = "average", nullable = false)
	val avg: Double
) {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	val id: Long = 0

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other == null || javaClass != other.javaClass) return false
		other as TripleResult
		return a == other.a && b == other.b && c == other.c
	}

	override fun hashCode(): Int = arrayOf(a, b, c).contentHashCode()
}
