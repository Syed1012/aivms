package de.syed.aivms.authservice.config

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.security.Key
import java.util.Date

@Component
class JwtUtil {

    private val expiration: Long = 1000 * 60 * 60 // 1 hour
    private val key: Key = Keys.secretKeyFor(SignatureAlgorithm.HS256)

    fun generateToken(username: String): String =
        Jwts.builder()
            .setSubject(username)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + expiration))
            .signWith(key)
            .compact()
}