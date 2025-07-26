package de.syed.aivms.authservice.repository

import de.syed.aivms.authservice.domain.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {
    fun findByEmail(email: String): Boolean
}