package de.syed.aivms.authservice.service.impl

import de.syed.aivms.authservice.domain.Role
import de.syed.aivms.authservice.domain.User
import de.syed.aivms.authservice.dto.UserRegisterRequest
import de.syed.aivms.authservice.exception.RegistrationException
import de.syed.aivms.authservice.repository.UserRepository
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.security.crypto.password.PasswordEncoder

class UserServiceImplTest {

    private lateinit var userRepository: UserRepository
    private lateinit var passwordEncoder: PasswordEncoder
    private lateinit var userService: UserServiceImpl

    @BeforeEach
    fun setUp() {
        userRepository = mock()
        passwordEncoder = mock()
        userService = UserServiceImpl(userRepository, passwordEncoder)
    }

    @Test
    fun `should register user successfully`() {
        // Prepare
        val request = UserRegisterRequest(
            email = "test@example.com",
            password = "password123",
            firstName = "Test",
            lastName = "User",
            phoneNumber = "1234567890"
        )

        // Test
        whenever(userRepository.existsByEmail(request.email)).thenReturn(false)
        whenever(passwordEncoder.encode(request.password)).thenReturn("hashedPassword")
        whenever(userRepository.save(any())).thenAnswer { invocation -> invocation.arguments[0] }

        val result: User = userService.registerUser(request)

        Assertions.assertThat(result.email).isEqualTo(request.email)
        Assertions.assertThat(result.password).isEqualTo("hashedPassword")
        Assertions.assertThat(result.firstName).isEqualTo(request.firstName)
        Assertions.assertThat(result.roles).isEqualTo(Role.USER)
        Assertions.assertThat(result.createdAt).isNotNull()

        // Validate
        verify(userRepository).save(any())
    }

    @Test
    fun `should always assign USER role regardless of input`() {
        // Prepare
        val request = UserRegisterRequest(
            email = "rolecheck@example.com",
            password = "password",
            firstName = "Role",
            lastName = "Checker",
            phoneNumber = "1234567890"
        )

        // Test
        whenever(userRepository.existsByEmail(request.email)).thenReturn(false)
        whenever(passwordEncoder.encode(request.password)).thenReturn("hashedPassword")
        whenever(userRepository.save(any())).thenAnswer { invocation -> invocation.arguments[0] }

        val result: User = userService.registerUser(request)

        // Assert that the role is always USER
        Assertions.assertThat(result.roles).isEqualTo(Role.USER)

        // Validate
        verify(userRepository).save(any())
    }


    @Test
    fun `should throw exception if email already exists`() {
        // Prepare
        val request = UserRegisterRequest(
            email = "duplicate@example.com",
            password = "password",
            firstName = "John",
            lastName = "Doe",
            phoneNumber = "1234567890"
        )

        // Test
        whenever(userRepository.existsByEmail(request.email)).thenReturn(true)

        // Validate
        Assertions.assertThatThrownBy { userService.registerUser(request) }
            .isInstanceOf(RegistrationException::class.java)
            .hasMessageContaining("Email '${request.email}' is already in use.")
    }


    @Test
    fun `should throw internal error when save fails`() {
        // Prepare
        val request = UserRegisterRequest(
            email = "fail@example.com",
            password = "password",
            firstName = "Fail",
            lastName = "Case",
            phoneNumber = "5551234567"
        )

        // Test
        whenever(userRepository.existsByEmail(request.email)).thenReturn(false)
        whenever(passwordEncoder.encode(request.password)).thenReturn("hashedPassword")
        whenever(userRepository.save(any())).thenThrow(RuntimeException("DB error"))

        // Validate
        Assertions.assertThatThrownBy { userService.registerUser(request) }
            .isInstanceOf(RegistrationException::class.java)
            .hasMessageContaining("Failed to register user due to internal error.")
    }
}