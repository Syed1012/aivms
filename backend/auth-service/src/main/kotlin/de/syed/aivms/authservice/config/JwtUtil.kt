package de.syed.aivms.authservice.config

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.security.Key
import java.util.*
import java.util.UUID

@Component
class JwtUtil {

    private val jwtExpirationMs: Long = 1000 * 60 * 30 // 30m for access token
    private val jwtRefreshExpirationMs: Long = 1000 * 60 * 60 * 24 * 7 // 7 days for refresh token
    private val key: Key = Keys.secretKeyFor(SignatureAlgorithm.HS256)

    /**
     * Generates a JWT access token.
     * @param userId The UUID of the user.
     * @param email The email of the user (subject of the token).
     * @param role The single role of the user.
     * @return The generated JWT access token.
     */
    fun generateToken(userId: UUID, email: String, role: String): String {
        val now = Date()
        val expiryDate = Date(now.time + jwtExpirationMs)

        return Jwts.builder()
            .setSubject(email)
            .claim("userId", userId.toString())
            .claim("role", role)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(key)
            .compact()
    }

    /**
     * Generates a JWT refresh token.
     * @param userId The UUID of the user.
     * @param email The email of the user (subject of the token).
     * @param role The single role of the user.
     * @return The generated JWT refresh token.
     */
    fun generateRefreshToken(userId: UUID, email: String, role: String): String {
        val now = Date()
        val expiryDate = Date(now.time + jwtRefreshExpirationMs)

        return Jwts.builder()
            .setSubject(email)
            .claim("userId", userId.toString())
            .claim("role", role)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(key)
            .compact()
    }

    /**
     * Retrieves the email (subject) from a JWT token.
     * @param token The JWT token.
     * @return The email extracted from the token.
     */
    fun getEmailFromToken(token: String): String {
        return Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .body
            .subject
    }

    /**
     * Retrieves the single role from a JWT token.
     * @param token The JWT token.
     * @return The role string extracted from the token, or an empty string if not found.
     */
    fun getRoleFromToken(token: String): String {
        val claims = Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .body

        return claims["role"] as? String
            ?: throw IllegalStateException("Role claim is missing or not a String in the token.")
    }

    /**
     * Retrieves the user ID from a JWT token.
     * @param token The JWT token.
     * @return The UUID of the user extracted from the token, or null if not found.
     */
    fun getUserIdFromToken(token: String): UUID?{
        val claims = Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .body

        return claims["userId"]?.let { UUID.fromString(it.toString()) }
    }

    /**
     * Validates a JWT token.
     * @param token The JWT token to validate.
     * @return True if the token is valid, false otherwise.
     */
    fun validateToken(token: String): Boolean {
        return try {
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
            true
        } catch (ex: Exception) {
            false
        }
    }
}