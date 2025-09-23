package de.syed.vehicleservice.controller

import de.syed.vehicleservice.dto.VehicleCreateRequest
import de.syed.vehicleservice.dto.VehicleResponse
import de.syed.vehicleservice.dto.VehicleUpdateRequest
import de.syed.vehicleservice.service.VehicleService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.NotNull
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.net.URI
import java.util.*

/**
 * REST controller for vehicle management operations.
 * Provides CRUD operations for vehicles in the vehicle service microservice.
 *
 * @author Syed
 * @since 1.0
 */
@RestController
@RequestMapping("/api/v1/vehicles")
@Tag(name = "Vehicles", description = "Vehicle management endpoints")
@Validated
class VehicleController(
    private val vehicleService: VehicleService
) {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(VehicleController::class.java)
        private const val DEFAULT_PAGE_SIZE = 20
        private const val MAX_PAGE_SIZE = 100

        /**
         * Sanitizes input for safe logging by removing potentially dangerous characters
         * and limiting length to prevent log injection attacks.
         */
        private fun sanitizeForLogging(input: String?): String {
            return input?.let { value ->
                value.take(50) // Limit length
                    .replace(Regex("[\\r\\n\\t]"), "_") // Remove line breaks and tabs
                    .replace(Regex("[<>&\"']"), "_") // Remove HTML/XML special chars
                    .trim()
            } ?: "N/A"
        }
    }

    /**
     * Creates a new vehicle.
     *
     * @param request The vehicle creation request containing vehicle details
     * @return ResponseEntity containing the created vehicle with 201 status
     */
    @Operation(
        summary = "Create a new vehicle",
        description = "Creates a new vehicle with the provided details. Registration number must be unique."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "Vehicle created successfully",
                content = [Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = VehicleResponse::class)
                )]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid request data or validation error",
                content = [Content()]
            ),
            ApiResponse(
                responseCode = "409",
                description = "Vehicle with this registration already exists",
                content = [Content()]
            ),
            ApiResponse(
                responseCode = "500",
                description = "Internal server error",
                content = [Content()]
            )
        ]
    )
    @PostMapping(
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @Suppress("kotlin:S5131") // Suppress XSS warning - Spring Boot handles JSON serialization safely
    fun createVehicle(
        @Valid @RequestBody request: VehicleCreateRequest
    ): ResponseEntity<VehicleResponse> {
        logger.info("Creating vehicle with registration: {}", sanitizeForLogging(request.registrationNumber))

        val createdVehicle = vehicleService.createVehicle(request)
        val location = URI.create("/api/v1/vehicles/${createdVehicle.id}")

        logger.info("Successfully created vehicle with ID: {}", createdVehicle.id)

        // Spring Boot's Jackson integration automatically escapes JSON content
        return ResponseEntity
            .created(location)
            .body(createdVehicle)
    }

    /**
     * Retrieves all vehicles with optional filtering.
     *
     * @param limit Optional limit for number of results (max 100)
     * @return ResponseEntity containing list of vehicles
     */
    @Operation(
        summary = "Get all vehicles",
        description = "Returns all vehicles. Use limit parameter to control result size for performance."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Vehicles retrieved successfully",
                content = [Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array = ArraySchema(schema = Schema(implementation = VehicleResponse::class))
                )]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid limit parameter",
                content = [Content()]
            ),
            ApiResponse(
                responseCode = "500",
                description = "Internal server error",
                content = [Content()]
            )
        ]
    )
    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getAllVehicles(
        @Parameter(description = "Maximum number of vehicles to return (1-100)", example = "50")
        @RequestParam(required = false, defaultValue = "50") limit: Int
    ): ResponseEntity<List<VehicleResponse>> {
        logger.debug("Retrieving vehicles with limit: {}", limit)

        // Validate and enforce limit bounds
        val effectiveLimit = when {
            limit < 1 -> 1
            limit > MAX_PAGE_SIZE -> MAX_PAGE_SIZE
            else -> limit
        }

        if (effectiveLimit != limit) {
            logger.debug("Adjusted limit from {} to {}", limit, effectiveLimit)
        }

        val vehicles = vehicleService.getAllVehicles()
        val limitedVehicles = if (vehicles.size > effectiveLimit) {
            logger.debug("Limiting results from {} to {} vehicles", vehicles.size, effectiveLimit)
            vehicles.take(effectiveLimit)
        } else {
            vehicles
        }

        logger.debug("Retrieved {} vehicles", limitedVehicles.size)

        return ResponseEntity.ok(limitedVehicles)
    }

    /**
     * Retrieves a specific vehicle by its ID.
     *
     * @param id The unique identifier of the vehicle
     * @return ResponseEntity containing the vehicle details
     */
    @Operation(
        summary = "Get vehicle by ID",
        description = "Returns a specific vehicle identified by its unique ID."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Vehicle found and returned",
                content = [Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = VehicleResponse::class)
                )]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid vehicle ID format",
                content = [Content()]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Vehicle not found",
                content = [Content()]
            ),
            ApiResponse(
                responseCode = "500",
                description = "Internal server error",
                content = [Content()]
            )
        ]
    )
    @GetMapping(
        value = ["/{id}"],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun getVehicleById(
        @Parameter(description = "Vehicle unique identifier", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
        @PathVariable @NotNull id: UUID
    ): ResponseEntity<VehicleResponse> {
        logger.debug("Retrieving vehicle with ID: {}", id)

        val vehicle = vehicleService.getVehicleById(id)

        logger.debug("Successfully retrieved vehicle with ID: {}", id)

        return ResponseEntity.ok(vehicle)
    }

    /**
     * Retrieves all vehicles belonging to a specific owner.
     *
     * @param ownerId The unique identifier of the owner
     * @param limit Optional limit for number of results (max 100)
     * @return ResponseEntity containing list of owner's vehicles
     */
    @Operation(
        summary = "Get vehicles by owner ID",
        description = "Returns all vehicles belonging to a specific owner with optional result limiting."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Vehicles retrieved successfully",
                content = [Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array = ArraySchema(schema = Schema(implementation = VehicleResponse::class))
                )]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid owner ID format or limit parameter",
                content = [Content()]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Owner not found or no vehicles found for owner",
                content = [Content()]
            ),
            ApiResponse(
                responseCode = "500",
                description = "Internal server error",
                content = [Content()]
            )
        ]
    )
    @GetMapping(
        value = ["/owner/{ownerId}"],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun getVehiclesByOwnerId(
        @Parameter(description = "Owner unique identifier", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
        @PathVariable @NotNull ownerId: UUID,
        @Parameter(description = "Maximum number of vehicles to return (1-100)", example = "50")
        @RequestParam(required = false, defaultValue = "50") limit: Int
    ): ResponseEntity<List<VehicleResponse>> {
        logger.debug("Retrieving vehicles for owner ID: {} with limit: {}", ownerId, limit)

        // Validate and enforce limit bounds
        val effectiveLimit = when {
            limit < 1 -> 1
            limit > MAX_PAGE_SIZE -> MAX_PAGE_SIZE
            else -> limit
        }

        val vehicles = vehicleService.getVehiclesByOwnerId(ownerId)
        val limitedVehicles = if (vehicles.size > effectiveLimit) {
            logger.debug("Limiting results from {} to {} vehicles for owner: {}", vehicles.size, effectiveLimit, ownerId)
            vehicles.take(effectiveLimit)
        } else {
            vehicles
        }

        logger.debug("Retrieved {} vehicles for owner ID: {}", limitedVehicles.size, ownerId)

        return ResponseEntity.ok(limitedVehicles)
    }

    /**
     * Updates an existing vehicle.
     *
     * @param id The unique identifier of the vehicle to update
     * @param request The vehicle update request containing updated details
     * @return ResponseEntity containing the updated vehicle
     */
    @Operation(
        summary = "Update vehicle",
        description = "Updates an existing vehicle. Partial updates are supported - only provided fields will be updated."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Vehicle updated successfully",
                content = [Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = VehicleResponse::class)
                )]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid request data, vehicle ID format, or validation error",
                content = [Content()]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Vehicle not found",
                content = [Content()]
            ),
            ApiResponse(
                responseCode = "409",
                description = "Conflict - registration number already exists for another vehicle",
                content = [Content()]
            ),
            ApiResponse(
                responseCode = "500",
                description = "Internal server error",
                content = [Content()]
            )
        ]
    )
    @PutMapping(
        value = ["/{id}"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun updateVehicle(
        @Parameter(description = "Vehicle unique identifier", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
        @PathVariable @NotNull id: UUID,
        @Valid @RequestBody request: VehicleUpdateRequest
    ): ResponseEntity<VehicleResponse> {
        logger.info("Updating vehicle with ID: {}", id)

        val updatedVehicle = vehicleService.updateVehicle(id, request)

        logger.info("Successfully updated vehicle with ID: {}", id)

        return ResponseEntity.ok(updatedVehicle)
    }

    /**
     * Deletes a specific vehicle.
     *
     * @param id The unique identifier of the vehicle to delete
     * @return ResponseEntity with no content and 204 status
     */
    @Operation(
        summary = "Delete vehicle",
        description = "Permanently deletes a vehicle from the system."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "204",
                description = "Vehicle deleted successfully",
                content = [Content()]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid vehicle ID format",
                content = [Content()]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Vehicle not found",
                content = [Content()]
            ),
            ApiResponse(
                responseCode = "500",
                description = "Internal server error",
                content = [Content()]
            )
        ]
    )
    @DeleteMapping(value = ["/{id}"])
    fun deleteVehicle(
        @Parameter(description = "Vehicle unique identifier", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
        @PathVariable @NotNull id: UUID
    ): ResponseEntity<Unit> {
        logger.info("Deleting vehicle with ID: {}", id)

        vehicleService.deleteVehicle(id)

        logger.info("Successfully deleted vehicle with ID: {}", id)

        return ResponseEntity.noContent().build()
    }
}