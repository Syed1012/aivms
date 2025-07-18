package de.syed.aivms.authservice.service.impl

import de.syed.aivms.authservice.domain.OtpCode
import de.syed.aivms.authservice.repository.OtpCodeRepository
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.time.Instant
import java.time.temporal.ChronoUnit

@Service
class OtpService(
    private val otpCodeRepository: OtpCodeRepository
) {
    private val logger = KotlinLogging.logger {}

    fun generateOtpForEmail(email: String): String {
        val otp = generateRandomOtp()
        val expiresAt = Instant.now().plus(5, ChronoUnit.MINUTES) // OTP valid for 5 minutes
        val otpCode = OtpCode(
            userEmail = email,
            otpCode = otp,
            expiresAt = expiresAt
        )
        otpCodeRepository.save(otpCode)
        logger.info { "Generated OTP for $email valid until $expiresAt" }
        return otp
    }

    fun verifyOtp(email: String, otp: String): Boolean {
        val otpCodeOpt = otpCodeRepository.findByUserEmailAndOtpCodeAndVerifiedFalse(email, otp)
        if (otpCodeOpt.isEmpty) {
            logger.warn { "OTP verification failed for $email with OTP $otp: Not found or already used" }
            return false
        }

        val otpCode = otpCodeOpt.orElse(null)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "No OTP found for $email")

        if (otpCode.expiresAt.isBefore(Instant.now())) {
            logger.warn { "OTP verification failed for $email: OTP expired" }
            return false
        }
        otpCode.verified = true
        otpCodeRepository.save(otpCode)
        logger.info { "OTP verified for $email" }
        return true
    }

    private fun generateRandomOtp(length: Int = 6): String {
        val digits = "0123456789"
        return (1..length)
            .map { digits.random() }
            .joinToString("")
    }
}
