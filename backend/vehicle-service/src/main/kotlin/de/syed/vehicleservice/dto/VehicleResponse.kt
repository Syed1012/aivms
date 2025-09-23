package de.syed.vehicleservice.dto

import de.syed.vehicleservice.domain.VehicleStatus
import java.time.Instant
import java.util.UUID

data class VehicleResponse(
    val id: UUID,
    val ownerId: UUID,
    val make: String,
    val model: String,
    val year: Int,
    val registrationNumber: String,
    val status: VehicleStatus,
    val createdAt: Instant,
    val updatedAt: Instant
)
