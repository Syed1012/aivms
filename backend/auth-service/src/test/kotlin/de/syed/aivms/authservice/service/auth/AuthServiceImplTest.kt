package de.syed.aivms.authservice.service.auth

import de.syed.aivms.authservice.config.JwtUtil
import de.syed.aivms.authservice.domain.Role
import de.syed.aivms.authservice.domain.User
import de.syed.aivms.authservice.dto.LoginRequest
import de.syed.aivms.authservice.dto.LoginResponse
import de.syed.aivms.authservice.exception.LoginExceptions
import de.syed.aivms.authservice.repository.UserRepository
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.Instant

class AuthServiceImplTest {
    private lateinit var userRepository: UserRepository
    private lateinit var passwordEncoder: PasswordEncoder
    private lateinit var jwtUtil: JwtUtil
    private lateinit var authService: AuthServiceImpl

    @BeforeEach
    fun setUp() {
        userRepository = mockk()
        passwordEncoder = mockk()
        jwtUtil = mockk()
        authService = AuthServiceImpl(userRepository, passwordEncoder, jwtUtil)
    }

    @Test
    fun `should login successfully with valid credentials`() {
        val request = LoginRequest(email = "user@example.com", password = "password123")
        val user = User(
            id = java.util.UUID.randomUUID(),
            email = request.email,
            password = "hashedPassword",
            firstName = "Test",
            lastName = "User",
            phoneNumber = "1234567890",
            role = Role.USER,
            address = "123 Main St",
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
        every { userRepository.findByEmail(request.email) } returns user
        every { passwordEncoder.matches(request.password, user.password) } returns true
        every { jwtUtil.generateToken(user.id!!, user.email, user.role.name) } returns "jwt-token"
        val response = authService.login(request)
        assert(response.accessToken == "jwt-token")
    }

    @Test
    fun `should throw exception for invalid credentials`() {
        val request = LoginRequest(email = "user@example.com", password = "wrongpass")
        val user = User(
            id = java.util.UUID.randomUUID(),
            email = request.email,
            password = "hashedPassword",
            firstName = "Test",
            lastName = "User",
            phoneNumber = "1234567890",
            role = Role.USER,
            address = "123 Main St",
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
        every { userRepository.findByEmail(request.email) } returns user
        every { passwordEncoder.matches(request.password, user.password) } returns false
        assertThatThrownBy { authService.login(request) }.isInstanceOf(LoginExceptions::class.java)
    }

    @Test
    fun `should throw exception if user not found`() {
        val request = LoginRequest(email = "nouser@example.com", password = "password123")
        every { userRepository.findByEmail(request.email) } returns null
        assertThatThrownBy { authService.login(request) }.isInstanceOf(LoginExceptions::class.java)
    }
}