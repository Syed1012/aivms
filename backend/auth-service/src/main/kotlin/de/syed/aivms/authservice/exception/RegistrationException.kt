package de.syed.aivms.authservice.exception

class RegistrationException(message: String) : RuntimeException(message) {

    companion object {
        fun emailAlreadyUsed(email: String) =
            "Email '$email' is already in use."

        fun invalidRole(role: Int?) =
            "Invalid role provided: $role"

        fun internalError() =
            "Failed to register user due to internal error."
    }
}
