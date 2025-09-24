package com.example.pythatriple_web_api.model

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.NaturalId
import java.time.LocalDateTime

@Entity
@Table(
	name = "calculation_results",
	uniqueConstraints = [
		UniqueConstraint(columnNames = ["hypotenuse_squared"], name = "uq_hypotsq")
	]
)
@Suppress("UNUSED")
class CalculationResult(
	@NaturalId
	@Column(
		name = "hypotenuse_squared", nullable = false, unique = true,
		columnDefinition = "INT UNSIGNED"
	)
	val hypotSq: Int,

	@ManyToOne
	@JoinColumn(
		name = "triple_id", nullable = true,
		foreignKey = ForeignKey(name = "fk_triple")
	)
	val triple: TripleResult? = null,

	@CreationTimestamp
	@Column(
		name = "calculated_at", nullable = false, updatable = false,
		columnDefinition = "TIMESTAMP"
	)
	val timestamp: LocalDateTime = LocalDateTime.now()
) {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	val id: Long = 0

	val isValid: Boolean
		get() = triple != null

	val isEmpty: Boolean
		get() = !isValid

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other == null || javaClass != other.javaClass) return false
		return hypotSq == (other as CalculationResult).hypotSq
	}

	override fun hashCode(): Int = hypotSq.hashCode()
}
