package de.syed.aivms.authservice.service.user

import de.syed.aivms.authservice.domain.User
import de.syed.aivms.authservice.dto.UserRegisterRequest

interface UserService {
    fun registerUser(request: UserRegisterRequest): User
}