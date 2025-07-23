package de.syed.aivms.authservice.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(
    name = "UserRegisterRequest",
    description = "Request body for registering a new user"
)
data class UserRegisterRequest(

    @Schema(
        description = "User's email address. Must be unique and valid.",
        example = "john.doe@example.com",
        required = true
    )
    val email: String,

    @Schema(
        description = "User's password (minimum 8 characters recommended)",
        example = "P@ssw0rd123",
        required = true
    )
    val password: String,

    @Schema(
        description = "User's first name",
        example = "John",
        required = true
    )
    val firstName: String,

    @Schema(
        description = "User's last name",
        example = "Doe",
        required = true
    )
    val lastName: String,

    @Schema(
        description = "User's phone number in international format",
        example = "+491234567890",
        required = true
    )
    val phoneNumber: String
)