package de.syed.aivms.authservice.service.impl

import de.syed.aivms.authservice.service.EmailService
import mu.KotlinLogging
import org.springframework.stereotype.Service

@Service
class EmailServiceImpl : EmailService {

    private val logger = KotlinLogging.logger {}

    override fun sendEmail(to: String, subject: String, body: String) {
        // Mock implementation for development
        logger.info { "Sending email to: $to\nSubject: $subject\nBody: $body" }
    }
}
