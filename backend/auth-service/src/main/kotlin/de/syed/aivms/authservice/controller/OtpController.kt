package de.syed.aivms.authservice.controller

import de.syed.aivms.authservice.service.email.EmailService
import de.syed.aivms.authservice.service.user.OtpService
import mu.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth/otp")
class OtpController(
    private val otpService: OtpService,
    private val emailService: EmailService
) {
    private val logger = KotlinLogging.logger {}

    @PostMapping("/send")
    fun sendOtp(@RequestParam email: String): ResponseEntity<String> {
        val otp = otpService.generateOtpForEmail(email)

        // Send OTP via email
        emailService.sendEmail(
            to = email,
            subject = "Your OTP Code",
            body = "Your OTP code is: $otp. It is valid for 5 minutes."
        )

        logger.info { "OTP sent for email $email (otp: $otp)" } // remove OTP log in production
        return ResponseEntity.ok("OTP sent to email: $email")
    }

    @PostMapping("/verify")
    fun verifyOtp(@RequestParam email: String, @RequestParam otp: String): ResponseEntity<String> {
        val valid = otpService.verifyOtp(email, otp)
        return if (valid) {
            ResponseEntity.ok("OTP verified successfully")
        } else {
            ResponseEntity.status(400).body("Invalid or expired OTP")
        }
    }
}
