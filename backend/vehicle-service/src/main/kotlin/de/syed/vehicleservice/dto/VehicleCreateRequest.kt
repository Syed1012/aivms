package de.syed.vehicleservice.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import java.util.UUID

data class VehicleCreateRequest(

    @field:NotNull(message = "Owner ID must not be null")
    var ownerId: UUID,

    @field:NotBlank(message = "ManufacturerName must not be null")
    var make: String,

    @field:NotBlank(message = "Model must not be null")
    var model: String,

    @field:Positive(message = "Vehicle year must be a positive integer")
    var year: Int,

    @field:NotBlank(message = "Registration number must not be null")
    var registrationNumber: String
)
