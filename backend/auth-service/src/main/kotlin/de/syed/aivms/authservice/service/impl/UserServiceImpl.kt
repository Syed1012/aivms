package de.syed.aivms.authservice.service.impl

import de.syed.aivms.authservice.domain.User
import de.syed.aivms.authservice.dto.UserRegisterRequest
import de.syed.aivms.authservice.exception.EmailAlreadyInUseException
import de.syed.aivms.authservice.repository.UserRepository
import de.syed.aivms.authservice.service.UserService
import mu.KotlinLogging
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) : UserService {

    private val logger = KotlinLogging.logger { }

    override fun registerUser(request: UserRegisterRequest): User {
        logger.info("Received registration request for email: ${request.email}")

        if (userRepository.existsByEmail(request.email)) {
            logger.warn { "Registration failed: Email already exists -> ${request.email}" }
            throw EmailAlreadyInUseException("Email ${request.email} is already registered")
        }

        val now = Instant.now()
        val user = User(
            email = request.email,
            password = passwordEncoder.encode(request.password),
            firstName = request.firstName,
            lastName = request.lastName,
            phoneNumber = request.phoneNumber,
            roles = "USER",
            createdAt = now,
            updatedAt = now
        )

        val savedUser = userRepository.save(user)
        logger.info { "User successfully registered: ${savedUser.email}" }

        return savedUser
    }
}