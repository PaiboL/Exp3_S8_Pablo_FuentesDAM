package cl.duoc.senalsegura.util

import cl.duoc.senalsegura.model.MessagePriority
import org.junit.Assert.assertEquals
import org.junit.Test

class MessageAnalyzerTest {

    @Test
    fun urgentKeywordCreatesEmergencyPriority() {
        val analysis = MessageAnalyzer.analyze("Necesito ayuda urgente")

        assertEquals(MessagePriority.EMERGENCY, analysis.priority)
        assertEquals(3, analysis.wordCount)
    }

    @Test
    fun nullableMessageIsHandledSafely() {
        val analysis = MessageAnalyzer.analyze(null)

        assertEquals(MessagePriority.NORMAL, analysis.priority)
        assertEquals(0, analysis.wordCount)
        assertEquals(0, analysis.readingSeconds)
    }

    @Test
    fun longMessageCreatesImportantPriority() {
        val message = "Este mensaje contiene más de diez palabras para facilitar una comunicación clara"

        assertEquals(MessagePriority.IMPORTANT, MessageAnalyzer.analyze(message).priority)
    }
}
