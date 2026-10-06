package cl.duoc.senalsegura.util

import cl.duoc.senalsegura.data.AlertRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AdvancedKotlinTest {

    @Test
    fun extensionPropertyCountsWords() {
        assertEquals(4, "  Necesito   ayuda por favor  ".wordCount)
    }

    @Test
    fun extensionFunctionNormalizesAccessibleMessage() {
        val result = "  Por favor,   escriba aquí. ".toAccessibleMessage()

        assertEquals("Por favor, escriba aquí.", result)
    }

    @Test(expected = InvalidAccessibleMessageException::class)
    fun extensionFunctionRejectsLongMessage() {
        "a".repeat(181).toAccessibleMessage()
    }

    @Test
    fun tryCatchFinallyHandlesExpectedErrorAndAlwaysCloses() {
        var errorHandled = false
        var processClosed = false

        val result = executeAccessibleAction(
            action = { " ".toAccessibleMessage() },
            onError = { errorHandled = true },
            onFinally = { processClosed = true }
        )

        assertNull(result)
        assertTrue(errorHandled)
        assertTrue(processClosed)
    }

    @Test
    fun alertExtensionBuildsTalkBackSummary() {
        val summary = AlertRepository.alerts.first().accessibilitySummary

        assertTrue(summary.contains("Necesito ayuda urgente"))
        assertFalse(summary.isBlank())
    }
}
