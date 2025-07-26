package de.syed.aivms.authservice.service.auth

import de.syed.aivms.authservice.dto.LoginRequest
import de.syed.aivms.authservice.dto.LoginResponse

interface AuthService {
    fun login(request: LoginRequest): LoginResponse
}