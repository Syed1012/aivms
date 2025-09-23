package de.syed.vehicleservice.domain

import jakarta.persistence.*
import java.time.Instant
import java.util.*

@Entity
@Table(name = "vehicles")
class Vehicle(

    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false, updatable = false, columnDefinition = "UUID")
    val id: UUID? = null,

    @Column(nullable = false)
    val ownerId: UUID,

    @Column(nullable = false)
    val make: String,

    @Column(nullable = false)
    val model: String,

    @Column(nullable = false)
    val year: Int,

    @Column(nullable = false, unique = true)
    val registrationNumber: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val status: VehicleStatus = VehicleStatus.RESERVED,

    @Column(nullable = false)
    val createdAt: Instant = Instant.now(),

    @Column(nullable = false)
    val updatedAt: Instant = Instant.now()
) {

    // Equal and hashCode should only depend on ID
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Vehicle) return false
        return id != null && id == other.id
    }

    override fun hashCode(): Int = id?.hashCode() ?: 0

    override fun toString(): String {
        return "Vehicle(id=$id, registrationNumber='$registrationNumber')"
    }
}