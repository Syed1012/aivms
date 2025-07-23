package de.syed.aivms.authservice.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description="Login response containing JWT access token")
data class LoginResponse(
    @Schema(example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    val accessToken: String
)
