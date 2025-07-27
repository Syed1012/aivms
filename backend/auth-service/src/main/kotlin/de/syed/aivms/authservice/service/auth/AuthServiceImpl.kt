package de.syed.aivms.authservice.service.auth

import de.syed.aivms.authservice.config.JwtUtil
import de.syed.aivms.authservice.dto.LoginRequest
import de.syed.aivms.authservice.dto.LoginResponse
import de.syed.aivms.authservice.exception.LoginExceptions
import de.syed.aivms.authservice.repository.UserRepository
import mu.KotlinLogging
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthServiceImpl (
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtUtil: JwtUtil
) : AuthService {

    private val logger = KotlinLogging.logger { }

    override fun login(request: LoginRequest): LoginResponse {
        logger.info("Attempting login for email: ${request.email}")

        val user = userRepository.findByEmail(request.email)
            ?: run{
                logger.warn{"Login failed: User not found for email: ${request.email}"}
                throw LoginExceptions(LoginExceptions.userNotFound(request.email))
            }

        if(!passwordEncoder.matches(request.password, user.password)){
            logger.warn { "Login failed: Incorrect password for email: ${request.email}" }
            throw LoginExceptions(LoginExceptions.invalidCredentials())
        }

        val accessToken = jwtUtil.generateToken(
            userId = user.id ?: throw LoginExceptions(LoginExceptions.internalError()),
            email = user.email,
            role = user.role.name
        )

        logger.info { "Login successful for email: ${request.email}" }

        return LoginResponse(accessToken)
    }
}