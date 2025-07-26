package de.syed.aivms.authservice.service.auth

import de.syed.aivms.authservice.config.JwtUtil
import de.syed.aivms.authservice.dto.LoginRequest
import de.syed.aivms.authservice.dto.LoginResponse
import de.syed.aivms.authservice.exception.LoginExceptions
import de.syed.aivms.authservice.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthServiceImpl (
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtUtil: JwtUtil
) : AuthService {

    override fun login(request: LoginRequest): LoginResponse {
        val user = userRepository.findByEmail(request.email)
//            ?: throw LoginExceptions(LoginExceptions.userNotFound(request.email))

//        if(!passwordEncoder.matches(request.password, user.password)){
//            throw LoginExceptions(LoginExceptions.invalidCredentials())
//        }
//
//        val token = jwtUtil.generateToken(
//            userId = user.id ?: throw IllegalStateException("User ID cannot be null"),
//            email = user.email,
//            roles = listOf(user.roles.name)
//        )

        val token = "1"

        return LoginResponse(token)
    }
}