package de.syed.vehicleservice.mapper

import de.syed.vehicleservice.domain.Vehicle
import de.syed.vehicleservice.dto.VehicleCreateRequest
import de.syed.vehicleservice.dto.VehicleResponse
import de.syed.vehicleservice.dto.VehicleUpdateRequest
import org.mapstruct.*
import java.time.Instant
import java.util.*

@Mapper(
    componentModel = "spring", // generates a Spring Bean automatically
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    imports = [UUID::class, Instant::class] // Import utilities for mapping
)
interface VehicleMapper {

    /**
     * Maps VehicleCreateRequest to Vehicle entity
     * Sets ID and timestamps during creation
     */
    @Mapping(target = "id", expression = "java(UUID.randomUUID())")
    @Mapping(target = "createdAt", expression = "java(Instant.now())")
    @Mapping(target = "updatedAt", expression = "java(Instant.now())")
    fun toEntity(request: VehicleCreateRequest): Vehicle

    /**
     * Maps Vehicle entity to VehicleResponse DTO
     * Used for API responses
     */
    fun toResponse(entity: Vehicle): VehicleResponse

    /**
     * Updates existing Vehicle entity from VehicleUpdateRequest
     * Ignores null values to perform partial updates
     * Updates the updatedAt timestamp
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true) // Never update ID
    @Mapping(target = "createdAt", ignore = true) // Never update creation timestamp
    @Mapping(target = "updatedAt", expression = "java(Instant.now())")
    fun updateEntityFromRequest(
        request: VehicleUpdateRequest,
        @MappingTarget entity: Vehicle
    )

    /**
     * Maps list of entities to list of responses
     * Useful for bulk operations
     */
    fun toResponseList(entities: List<Vehicle>): List<VehicleResponse>

    /**
     * Custom mapping method for complex transformations
     * You can add custom logic here if needed
     */
    @Named("customMapping")
    fun customMapping(value: String): String {
        return value.uppercase().trim()
    }
}