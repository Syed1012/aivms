package de.syed.vehicleservice.repository

import de.syed.vehicleservice.domain.Vehicle
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface VehicleRepository : JpaRepository<Vehicle, UUID>{
    fun findByRegistrationNumber(registrationNumber: String): Vehicle?
    fun findByOwnerId(ownerId: UUID) : List<Vehicle>
    fun existsByRegistrationNumber(registrationNumber: String): Boolean
}