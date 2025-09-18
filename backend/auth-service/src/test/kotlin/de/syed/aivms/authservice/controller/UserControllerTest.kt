package de.syed.aivms.authservice.controller

import de.syed.aivms.authservice.dto.UserRegisterRequest
import de.syed.aivms.authservice.exception.RegistrationException
import de.syed.aivms.authservice.service.user.UserService
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import io.mockk.mockk
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import com.fasterxml.jackson.databind.ObjectMapper

@WebMvcTest(UserController::class)
class UserControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    private val userService: UserService = mockk(relaxed = true)

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun `should register user successfully`() {
        val request = UserRegisterRequest(
            email = "newuser@example.com",
            password = "password123",
            firstName = "New",
            lastName = "User",
            phoneNumber = "1234567890"
        )
        val user = de.syed.aivms.authservice.domain.User(
            id = java.util.UUID.randomUUID(),
            email = request.email,
            password = "hashedPassword",
            firstName = request.firstName,
            lastName = request.lastName,
            phoneNumber = request.phoneNumber,
            role = de.syed.aivms.authservice.domain.Role.USER,
            address = "123 Main St",
            createdAt = java.time.Instant.now(),
            updatedAt = java.time.Instant.now()
        )
        every { userService.registerUser(request) } returns user

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().string("User registered successfully"))
    }

    @Test
    fun `should return error for existing user`() {
        val request = UserRegisterRequest(
            email = "existing@example.com",
            password = "password123",
            firstName = "Existing",
            lastName = "User",
            phoneNumber = "1234567890"
        )
        every { userService.registerUser(request) } throws RegistrationException("User already exists")

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(MockMvcResultMatchers.status().is5xxServerError)
    }

    @Test
    fun `should return validation error for invalid input`() {
        val request = mapOf("email" to "invalid@example.com") // missing required fields
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(MockMvcResultMatchers.status().is4xxClientError)
    }
}
