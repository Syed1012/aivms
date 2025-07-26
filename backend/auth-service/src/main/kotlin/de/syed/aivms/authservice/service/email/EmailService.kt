package de.syed.aivms.authservice.service.email

interface EmailService {
    fun sendEmail(to: String, subject: String, body: String)
}