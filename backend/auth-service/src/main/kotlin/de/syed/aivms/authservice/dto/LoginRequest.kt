package de.syed.aivms.authservice.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Login request payload containing email and password")
data class LoginRequest(

    @Schema(example = "abc.efg@exmaple.com")
    val email: String,

    @Schema(example = "Pss13!2@1")
    val password: String
)
