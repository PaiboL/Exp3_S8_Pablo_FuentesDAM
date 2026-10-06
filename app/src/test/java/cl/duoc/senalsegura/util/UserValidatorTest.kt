package cl.duoc.senalsegura.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class UserValidatorTest {

    @Test
    fun validEmailRequiresAtSignAndDomainPoint() {
        assertTrue(UserValidator.isValidEmail("pablo@duoc.cl"))
        assertFalse(UserValidator.isValidEmail("pablo@duoc"))
        assertFalse(UserValidator.isValidEmail("pablo duoc.cl"))
    }

    @Test
    fun validRegistrationReturnsNull() {
        val error = UserValidator.validateRegistration(
            name = "Pablo Fuentes",
            email = "pablo@duoc.cl",
            password = "123456",
            confirmPassword = "123456",
            termsAccepted = true
        )

        assertNull(error)
    }

    @Test
    fun differentPasswordsReturnAnError() {
        val error = UserValidator.validateRegistration(
            name = "Pablo Fuentes",
            email = "pablo@duoc.cl",
            password = "123456",
            confirmPassword = "567890",
            termsAccepted = true
        )

        assertEquals("Las contraseñas no coinciden", error)
    }
}
