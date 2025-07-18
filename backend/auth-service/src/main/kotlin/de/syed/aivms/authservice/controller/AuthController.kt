package de.syed.aivms.authservice.controller

import de.syed.aivms.authservice.dto.UserRegisterRequest
import de.syed.aivms.authservice.service.UserService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthController (
     private val userService: UserService
    // Uncomment the above line and inject UserService when implementing registration logic
) {

    @PostMapping("/register")
    fun registerUser(@Valid @RequestBody request: UserRegisterRequest): ResponseEntity<String>{
        userService.registerUser(request)
        return ResponseEntity.ok("User registered successfully")
    }
}