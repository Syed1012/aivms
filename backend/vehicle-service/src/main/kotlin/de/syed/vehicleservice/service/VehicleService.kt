package de.syed.vehicleservice.service

import de.syed.vehicleservice.dto.VehicleCreateRequest
import de.syed.vehicleservice.dto.VehicleResponse
import de.syed.vehicleservice.dto.VehicleUpdateRequest
import java.util.UUID

interface VehicleService {
    fun createVehicle(request: VehicleCreateRequest): VehicleResponse
    fun getAllVehicles(): List<VehicleResponse>
    fun getVehicleById(id: UUID): VehicleResponse
    fun getVehiclesByOwnerId(ownerId: UUID): List<VehicleResponse>
    fun updateVehicle(id: UUID, request: VehicleUpdateRequest): VehicleResponse
    fun deleteVehicle(id: UUID)
}