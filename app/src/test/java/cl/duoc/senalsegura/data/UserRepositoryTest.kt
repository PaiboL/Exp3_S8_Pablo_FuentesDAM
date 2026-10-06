package cl.duoc.senalsegura.data

import cl.duoc.senalsegura.model.User
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class UserRepositoryTest {

    @Test
    fun authenticateAcceptsValidCredentials() {
        assertNotNull(UserRepository.authenticate("pablo@duoc.cl", "1234"))
    }

    @Test
    fun authenticateRejectsInvalidCredentials() {
        assertNull(UserRepository.authenticate("pablo@duoc.cl", "incorrecta"))
    }

    @Test
    fun registerAddsANewUserAndRejectsDuplicateEmail() {
        val email = "prueba.unica@senalsegura.cl"
        val firstAttempt = UserRepository.register(
            User("Usuario Prueba", email, "5678")
        )
        val secondAttempt = UserRepository.register(
            User("Usuario Repetido", email, "9999")
        )

        assertTrue(firstAttempt)
        assertFalse(secondAttempt)
        assertTrue(UserRepository.containsEmail(email))
    }
}

