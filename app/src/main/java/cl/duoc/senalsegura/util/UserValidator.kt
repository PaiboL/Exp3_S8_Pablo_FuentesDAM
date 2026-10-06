package cl.duoc.senalsegura.util

object UserValidator {
    fun isValidEmail(email: String): Boolean {
        val normalizedEmail = email.trim()
        val domain = normalizedEmail.substringAfter("@", missingDelimiterValue = "")

        return normalizedEmail.contains("@") &&
            domain.contains(".") &&
            !normalizedEmail.contains(" ")
    }

    fun validateRegistration(
        name: String,
        email: String,
        password: String,
        confirmPassword: String,
        termsAccepted: Boolean
    ): String? {
        return when {
            name.isBlank() || email.isBlank() || password.isBlank() ->
                "Completa todos los campos obligatorios"
            !isValidEmail(email) -> "Ingresa un correo válido"
            password.length < 6 -> "La contraseña debe tener al menos 6 caracteres"
            password != confirmPassword -> "Las contraseñas no coinciden"
            !termsAccepted -> "Debes aceptar el uso de los datos para la demostración"
            else -> null
        }
    }
}
