package de.syed.aivms.authservice.repository

import de.syed.aivms.authservice.domain.OtpCode
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional
import java.util.UUID

interface OtpCodeRepository : JpaRepository<OtpCode, UUID> {
    fun findByUserEmailAndOtpCodeAndVerifiedFalse(userEmail: String, otpCode: String): Optional<OtpCode>
}