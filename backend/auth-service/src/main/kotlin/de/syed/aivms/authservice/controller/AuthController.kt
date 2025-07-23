package de.syed.aivms.authservice.controller

import de.syed.aivms.authservice.dto.UserRegisterRequest
import de.syed.aivms.authservice.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication",
    description = "Endpoints for user authentication and registration")
class AuthController (
     private val userService: UserService
) {

    @PostMapping("/register")
    @Operation(
        summary = "Register a new user",
        description = "Register a new user into the system with the details",
        operationId = "registerUser"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "User registered successfully"),
            ApiResponse(responseCode = "400", description = "Invalid input data"),
            ApiResponse(responseCode = "409", description = "User already exists")
        ]
    )
    fun registerUser(@Valid @RequestBody request: UserRegisterRequest): ResponseEntity<String>{
        userService.registerUser(request)
        return ResponseEntity.ok("User registered successfully")
    }
}