package de.syed.aivms.authservice.domain

import jakarta.persistence.*
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "otp_codes")
data class OtpCode(
    @Id
    @GeneratedValue
    val id: UUID? = null,

    @Column(name = "user_email", nullable = false)
    val userEmail: String,

    @Column(name = "otp_code", nullable = false)
    val otpCode: String,

    @Column(name = "expires_at", nullable = false)
    val expiresAt: Instant,

    @Column(name = "verified", nullable = false)
    var verified: Boolean = false,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now(),
)
