package cl.duoc.senalsegura.util

import cl.duoc.senalsegura.model.MessageAnalysis
import cl.duoc.senalsegura.model.MessagePriority

object MessageAnalyzer {
    private const val WORDS_PER_MINUTE: Int = 150

    private val emergencyKeywords: List<String> = listOf(
        "ayuda",
        "emergencia",
        "peligro",
        "evacuar",
        "urgente"
    )

    fun analyze(message: String?): MessageAnalysis {
        val normalizedMessage: String = message?.trim().orEmpty()
        val wordCount: Int = normalizedMessage.wordCount
        val readingSeconds: Int = if (wordCount == 0) {
            0
        } else {
            maxOf(1, (wordCount * 60) / WORDS_PER_MINUTE)
        }

        val priority: MessagePriority = when {
            normalizedMessage.isBlank() -> MessagePriority.NORMAL
            containsEmergencyKeyword(normalizedMessage) || normalizedMessage.contains("!") ->
                MessagePriority.EMERGENCY
            wordCount >= 10 -> MessagePriority.IMPORTANT
            else -> MessagePriority.NORMAL
        }

        return MessageAnalysis(
            priority = priority,
            wordCount = wordCount,
            readingSeconds = readingSeconds
        )
    }

    internal fun countWords(message: String): Int {
        return message.wordCount
    }

    private fun containsEmergencyKeyword(message: String): Boolean {
        val lowercaseMessage = message.lowercase()

        for (keyword in emergencyKeywords) {
            if (lowercaseMessage.contains(keyword)) return true
        }

        return false
    }
}
