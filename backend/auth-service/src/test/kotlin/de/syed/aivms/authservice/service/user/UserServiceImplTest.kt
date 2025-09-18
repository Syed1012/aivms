package de.syed.aivms.authservice.service.user

import de.syed.aivms.authservice.domain.Role
import de.syed.aivms.authservice.domain.User
import de.syed.aivms.authservice.dto.UserRegisterRequest
import de.syed.aivms.authservice.exception.RegistrationException
import de.syed.aivms.authservice.repository.UserRepository
import io.mockk.every // Import MockK's every
import io.mockk.mockk // Import MockK's mockk
import io.mockk.verify // Import MockK's verify
import io.mockk.confirmVerified // Import MockK's confirmVerified
import io.mockk.clearAllMocks // Import MockK's clearAllMocks
import io.mockk.slot // Import MockK's slot for argument capturing
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.UUID

class UserServiceImplTest {

    private lateinit var userRepository: UserRepository
    private lateinit var passwordEncoder: PasswordEncoder
    private lateinit var userService: UserServiceImpl

    @BeforeEach
    fun setUp() {
        userRepository = mockk()
        passwordEncoder = mockk()
        userService = UserServiceImpl(userRepository, passwordEncoder)
        clearAllMocks()
    }

    @Test
    fun `should register user successfully`() {
        // Prepare test data
        val request = UserRegisterRequest(
            email = "test@example.com",
            password = "password123",
            firstName = "Test",
            lastName = "User",
            phoneNumber = "1234567890"
        )
        val hashedPassword = "hashedPassword"
        val capturedUserSlot = slot<User>()

        // Define mock behavior using MockK's 'every'
        every { userRepository.existsByEmail(request.email) } returns false
        every { passwordEncoder.encode(request.password) } returns hashedPassword
        every { userRepository.save(capture(capturedUserSlot)) } answers {
            val u = capturedUserSlot.captured
            de.syed.aivms.authservice.domain.User(
                id = java.util.UUID.randomUUID(),
                email = u.email,
                password = u.password,
                firstName = u.firstName,
                lastName = u.lastName,
                phoneNumber = u.phoneNumber,
                role = u.role,
                address = "123 Main St",
                createdAt = java.time.Instant.now(),
                updatedAt = java.time.Instant.now()
            )
        }

        // Execute the method under test
        val result: User = userService.registerUser(request)

        // Assertions
        Assertions.assertThat(result.email).isEqualTo(request.email)
        Assertions.assertThat(result.password).isEqualTo(hashedPassword)
        Assertions.assertThat(result.firstName).isEqualTo(request.firstName)
        Assertions.assertThat(result.role).isEqualTo(Role.USER)
        Assertions.assertThat(result.createdAt).isNotNull()
        Assertions.assertThat(result.updatedAt).isNotNull()
        Assertions.assertThat(result.id).isNotNull() // Now this assertion should pass

        // Verify interactions with mocks using MockK's 'verify'
        verify(exactly = 1) { userRepository.existsByEmail(request.email) }
        verify(exactly = 1) { passwordEncoder.encode(request.password) }
        verify(exactly = 1) { userRepository.save(any()) }

        // Ensure no other unexpected interactions with the mocks
        confirmVerified(userRepository, passwordEncoder)
    }

/*    @Test
    fun `should always assign USER role regardless of input`() {
        // Prepare test data
        val request = UserRegisterRequest(
            email = "rolecheck@example.com",
            password = "password",
            firstName = "Role",
            lastName = "Checker",
            phoneNumber = "1234567890"
        )
        val hashedPassword = "hashedPassword"
        val capturedUserSlot = slot<User>()

        // Define mock behavior
        every { userRepository.existsByEmail(request.email) } returns false
        every { passwordEncoder.encode(request.password) } returns hashedPassword
        every { userRepository.save(capture(capturedUserSlot)) } answers { capturedUserSlot.captured.copy(id = java.util.UUID.randomUUID(), address = "123 Main St", createdAt = java.time.Instant.now(), updatedAt = java.time.Instant.now()) }

        // Execute the method under test
        val result: User = userService.registerUser(request)

        // Assert that the role is always USER
        Assertions.assertThat(result.role).isEqualTo(Role.USER)

        // Verify interactions
        verify(exactly = 1) { userRepository.existsByEmail(request.email) }
        verify(exactly = 1) { passwordEncoder.encode(request.password) }
        verify(exactly = 1) { userRepository.save(any()) }
        confirmVerified(userRepository, passwordEncoder)
    }
*/

    @Test
    fun `should throw exception if email already exists`() {
        // Prepare test data
        val request = UserRegisterRequest(
            email = "duplicate@example.com",
            password = "password",
            firstName = "John",
            lastName = "Doe",
            phoneNumber = "1234567890"
        )

        // Define mock behavior: email already exists
        every { userRepository.existsByEmail(request.email) } returns true

        // Execute and assert that an exception is thrown
        Assertions.assertThatThrownBy { userService.registerUser(request) }
            .isInstanceOf(RegistrationException::class.java)
            .hasMessageContaining("Email '${request.email}' is already in use.")

        // Verify interactions: only existsByEmail should be called
        verify(exactly = 1) { userRepository.existsByEmail(request.email) }
        confirmVerified(userRepository)
    }


    @Test
    fun `should throw internal error when save fails`() {
        // Prepare test data
        val request = UserRegisterRequest(
            email = "fail@example.com",
            password = "password",
            firstName = "Fail",
            lastName = "Case",
            phoneNumber = "5551234567"
        )
        val hashedPassword = "hashedPassword"

        // Define mock behavior: save throws an exception
        every { userRepository.existsByEmail(request.email) } returns false
        every { passwordEncoder.encode(request.password) } returns hashedPassword
        every { userRepository.save(any()) } throws RuntimeException("DB error")

        // Execute and assert that an exception is thrown
        Assertions.assertThatThrownBy { userService.registerUser(request) }
            .isInstanceOf(RegistrationException::class.java)
            .hasMessageContaining("Failed to register user due to internal error.")

        // Verify interactions
        verify(exactly = 1) { userRepository.existsByEmail(request.email) }
        verify(exactly = 1) { passwordEncoder.encode(request.password) }
        verify(exactly = 1) { userRepository.save(any()) }
        confirmVerified(userRepository, passwordEncoder)
    }

    @Test
    fun `should throw RegistrationException if user already exists`() {
        val request = UserRegisterRequest(
            email = "existing@example.com",
            password = "password123",
            firstName = "Existing",
            lastName = "User",
            phoneNumber = "1234567890"
        )
        every { userRepository.existsByEmail(request.email) } returns true
        Assertions.assertThatThrownBy { userService.registerUser(request) }
            .isInstanceOf(RegistrationException::class.java)
    }

/*    @Test
    fun `should encode password before saving user`() {
        val request = UserRegisterRequest(
            email = "encode@example.com",
            password = "plainPassword",
            firstName = "Encode",
            lastName = "User",
            phoneNumber = "1234567890"
        )
        val hashedPassword = "hashedPassword"
        val capturedUserSlot = slot<User>()
        every { userRepository.existsByEmail(request.email) } returns false
        every { passwordEncoder.encode(request.password) } returns hashedPassword
        every { userRepository.save(capture(capturedUserSlot)) } answers { capturedUserSlot.captured.copy(id = java.util.UUID.randomUUID(), address = "123 Main St", createdAt = java.time.Instant.now(), updatedAt = java.time.Instant.now()) }
        userService.registerUser(request)
        Assertions.assertThat(capturedUserSlot.captured.password).isEqualTo(hashedPassword)
    }
 */

    @Test
    fun `should throw exception for invalid input`() {
        val request = UserRegisterRequest(
            email = "",
            password = "",
            firstName = "",
            lastName = "",
            phoneNumber = ""
        )
        every { userRepository.existsByEmail(request.email) } returns false
        every { passwordEncoder.encode(request.password) } returns ""
        Assertions.assertThatThrownBy { userService.registerUser(request) }.isInstanceOf(Exception::class.java)
    }
}
