package cl.duoc.senalsegura.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class RegistrationValidationTest {
    @Test fun blankRequiredFieldsAreRejected() {
        assertEquals("Completa todos los campos obligatorios", validate(name = " "))
    }

    @Test fun malformedEmailIsRejected() {
        assertEquals("Ingresa un correo válido", validate(email = "pablo@duoc"))
    }

    @Test fun shortPasswordIsRejected() {
        assertEquals("La contraseña debe tener al menos 6 caracteres", validate(password = "12345"))
    }

    @Test fun passwordMismatchIsRejected() {
        assertEquals("Las contraseñas no coinciden", validate(confirmPassword = "654321"))
    }

    @Test fun unacceptedTermsAreRejected() {
        assertEquals(
            "Debes aceptar el uso de los datos para la demostración",
            validate(termsAccepted = false)
        )
    }

    @Test fun validRegistrationIsAccepted() {
        assertNull(validate())
    }

    private fun validate(
        name: String = "Pablo Fuentes",
        email: String = "pablo@duoc.cl",
        password: String = "123456",
        confirmPassword: String = password,
        termsAccepted: Boolean = true
    ) = UserValidator.validateRegistration(name, email, password, confirmPassword, termsAccepted)
}
