package de.syed.aivms.authservice.config

import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.security.Key
import java.util.*
import java.util.UUID

@Component
class JwtUtil (
    @Value("\${jwt.secret}") private val secret: String,
    @Value("\${jwt.expiration}") private val jwtExpirationMs: Long
){
    private val key: Key = Keys.hmacShaKeyFor(secret.toByteArray())

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
            .claim("role", role)
            .claim("userId", userId)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(key)
            .compact()
    }

    /**
     * Retrieves the email (subject) from a JWT token.
     * @param token The JWT token.
     */
    fun getEmailFromToken(token: String): String =
        parseClaims(token).subject

    /**
     * Retrieves the single role from a JWT token.
     * @param token The JWT token.
     */
    fun getRoleFromToken(token: String): String =
        parseClaims(token)["role"] as String

    /**
     * Retrieves the user ID from a JWT token.
     * @param token The JWT token.
     */
    fun getUserIdFromToken(token: String): String =
        parseClaims(token)["userId"] as String


    fun validateToken(token: String): Boolean =
        try {
            parseClaims(token)
            true
        } catch (ex: ExpiredJwtException) {
            false
        } catch (ex: JwtException) {
            false
        }

    /**
     * Parses the claims from a JWT token.
     * @param token The JWT token to parse.
     * @return The claims contained in the token.
     */
    private fun parseClaims(token: String): Claims =
        Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .body
}