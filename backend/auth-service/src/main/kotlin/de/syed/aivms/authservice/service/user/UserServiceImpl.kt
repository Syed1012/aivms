package de.syed.aivms.authservice.service.user

import de.syed.aivms.authservice.domain.Role
import de.syed.aivms.authservice.domain.User
import de.syed.aivms.authservice.dto.UserRegisterRequest
import de.syed.aivms.authservice.exception.RegistrationException
import de.syed.aivms.authservice.repository.UserRepository
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
            throw RegistrationException(RegistrationException.Companion.emailAlreadyUsed(request.email))
        }

        val now = Instant.now()
        val role = Role.USER  // Default role on registration

        val user = User(
            email = request.email,
            password = passwordEncoder.encode(request.password),
            firstName = request.firstName,
            lastName = request.lastName,
            phoneNumber = request.phoneNumber,
            role = role,
            createdAt = now,
            updatedAt = now
        )

        return try {
            val savedUser = userRepository.save(user)
            logger.info { "User with email ${savedUser.email} registered successfully." }
            savedUser
        } catch (ex: Exception) {
            logger.error(ex) { "Unexpected error while registering user with email: ${request.email}" }
            throw RegistrationException(RegistrationException.Companion.internalError())
        }
    }
}