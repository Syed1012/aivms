package de.syed.aivms.authservice.exception

class LoginExceptions(message: String) : RuntimeException(message) {

    companion object {
        fun invalidCredentials() =
            "Invalid email or password."

        fun userNotFound(email: String) =
            "User with email '$email' not found."

        fun internalError() =
            "Failed to login due to internal error."
    }
}