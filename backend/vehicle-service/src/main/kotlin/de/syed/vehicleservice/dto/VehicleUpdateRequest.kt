package de.syed.vehicleservice.dto

import de.syed.vehicleservice.domain.VehicleStatus
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive

data class VehicleUpdateRequest(

    @field:NotBlank(message = "Make must not be blank")
    val make: String?,

    @field:NotBlank(message = "Model must not be blank")
    val model: String?,

    @field:Positive(message = "Year must be positive")
    val year: Int?,

    @field:NotBlank(message = "RegistrationNumber must not be blank")
    val registrationNumber: String?,

    val status: VehicleStatus?
)
