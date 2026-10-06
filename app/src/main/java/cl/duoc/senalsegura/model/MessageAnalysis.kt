package cl.duoc.senalsegura.model

enum class MessagePriority(val label: String) {
    NORMAL("Normal"),
    IMPORTANT("Importante"),
    EMERGENCY("Emergencia")
}

data class MessageAnalysis(
    val priority: MessagePriority,
    val wordCount: Int,
    val readingSeconds: Int
)
