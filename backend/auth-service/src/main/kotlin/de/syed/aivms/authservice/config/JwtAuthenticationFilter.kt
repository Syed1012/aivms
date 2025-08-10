package de.syed.aivms.authservice.config

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import mu.KotlinLogging
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter

class JwtAuthenticationFilter (
    private val jwtUtil: JwtUtil
): OncePerRequestFilter(){
    private val log = KotlinLogging.logger { }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ){
        val authHeader = request.getHeader("Authorization")

        if(authHeader != null && authHeader.startsWith("Bearer")){
            val token = authHeader.substringAfter("Bearer").trim()

            try {
                if(jwtUtil.validateToken(token)){
                    val email = jwtUtil.getEmailFromToken(token)
                    val roleClaim = jwtUtil.getRoleFromToken(token)
                    val userId = jwtUtil.getUserIdFromToken(token)

                    val authority = SimpleGrantedAuthority("Role_$roleClaim")

                    val authentication = UsernamePasswordAuthenticationToken(
                        email,
                        null,
                        listOf(authority)
                    )

                    authentication.details = userId

                    SecurityContextHolder.getContext().authentication = authentication

                    logger.debug { "JWT authentication successful for user: $email with role: $roleClaim" }
                } else{
                    logger.debug { "JWT validation returned false for token." }
                    // token invalid/expired -> respond 401
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired JWT token")
                    return
                }
            } catch (ex: Exception){
                log.warn(ex) { "JWT processing failed: ${ex.message}" }
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired JWT token")
                return
            }
        }
        filterChain.doFilter(request, response)
    }
}