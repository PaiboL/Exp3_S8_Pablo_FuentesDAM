package cl.duoc.senalsegura.model

enum class AlertCategory(val label: String) {
    GENERAL("General"),
    SAFETY("Seguridad"),
    HEALTH("Salud")
}

public interface AccessibleContent {
    val symbol: String
    fun textForSpeech(): String
}

public abstract class AccessibleAlert protected constructor(
    val id: Int,
    val title: String,
    protected val message: String,
    val category: AlertCategory
) : AccessibleContent {

    fun visibleMessage(): String = message

    internal fun searchableText(): String {
        return "$title $message ${category.label}".lowercase()
    }
}

class EmergencyAlert(
    id: Int,
    title: String,
    message: String,
    category: AlertCategory,
    private val suggestedAction: String
) : AccessibleAlert(id, title, message, category) {

    override val symbol: String = "⚠"

    override fun textForSpeech(): String {
        // Polimorfismo: una alerta de emergencia añade la acción sugerida al mensaje base.
        return "$message. $suggestedAction"
    }

    fun actionDescription(): String = suggestedAction
}

class InformationAlert(
    id: Int,
    title: String,
    message: String,
    category: AlertCategory,
    override val symbol: String
) : AccessibleAlert(id, title, message, category) {

    override fun textForSpeech(): String {
        // Polimorfismo: una alerta informativa reproduce únicamente el mensaje visible.
        return message
    }
}
