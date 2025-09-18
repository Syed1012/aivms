package de.syed.aivms.authservice.controller

import de.syed.aivms.authservice.dto.LoginRequest
import de.syed.aivms.authservice.dto.LoginResponse
import de.syed.aivms.authservice.service.auth.AuthService
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

@WebMvcTest(AuthController::class)
class AuthControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    private val authService: AuthService = mockk(relaxed = true)

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun `should login successfully with valid credentials`() {
        val request = LoginRequest(email = "user@example.com", password = "password123")
        val response = LoginResponse(accessToken = "jwt-token")
        every { authService.login(request) } returns response

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.accessToken").value("jwt-token"))
    }

    @Test
    fun `should return error for invalid credentials`() {
        val request = LoginRequest(email = "user@example.com", password = "wrongpass")
        every { authService.login(request) } throws RuntimeException("Invalid credentials")

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(MockMvcResultMatchers.status().is5xxServerError)
    }

    @Test
    fun `should return validation error for missing fields`() {
        val request = mapOf("email" to "user@example.com") // missing password
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(MockMvcResultMatchers.status().is4xxClientError)
    }
}
