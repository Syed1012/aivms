package de.syed.aivms.authservice.service

interface EmailService {
    fun sendEmail(to: String, subject: String, body: String)
}
