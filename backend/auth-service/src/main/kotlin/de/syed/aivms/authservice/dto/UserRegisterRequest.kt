package de.syed.aivms.authservice.dto

data class UserRegisterRequest(
    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String
)
