package cl.duoc.senalsegura.data

import cl.duoc.senalsegura.model.AccessibleAlert
import cl.duoc.senalsegura.model.AlertCategory
import cl.duoc.senalsegura.model.EmergencyAlert
import cl.duoc.senalsegura.model.InformationAlert
import cl.duoc.senalsegura.util.buildAlertPredicate

object AlertRepository {
    private val alertArray: Array<AccessibleAlert> = arrayOf(
        EmergencyAlert(
            id = 1,
            title = "Necesito ayuda urgente",
            message = "Necesito ayuda inmediata",
            category = AlertCategory.HEALTH,
            suggestedAction = "Llame a emergencias, por favor"
        ),
        EmergencyAlert(
            id = 2,
            title = "Evacuación",
            message = "Debemos evacuar por la salida señalizada",
            category = AlertCategory.SAFETY,
            suggestedAction = "Avise mediante señales visuales y vibración"
        ),
        InformationAlert(
            id = 3,
            title = "Comunicación escrita",
            message = "Por favor, comuníquese conmigo por escrito",
            category = AlertCategory.GENERAL,
            symbol = "✍"
        ),
        InformationAlert(
            id = 4,
            title = "Esperar respuesta",
            message = "Espere un momento mientras leo su mensaje",
            category = AlertCategory.GENERAL,
            symbol = "⏳"
        ),
        InformationAlert(
            id = 5,
            title = "Equipo de protección",
            message = "Necesito mi equipo de protección antes de continuar",
            category = AlertCategory.SAFETY,
            symbol = "🦺"
        )
    )

    private val categoryDescriptions: Map<AlertCategory, String> = mapOf(
        AlertCategory.GENERAL to "Mensajes para situaciones cotidianas.",
        AlertCategory.SAFETY to "Indicaciones para prevenir riesgos.",
        AlertCategory.HEALTH to "Mensajes relacionados con salud y emergencias."
    )

    val alerts: List<AccessibleAlert>
        get() = alertArray.toList()

    val categories: List<AlertCategory>
        get() = AlertCategory.entries.toList()

    fun filterAlerts(
        category: AlertCategory?,
        favoriteIds: Set<Int>,
        favoritesOnly: Boolean,
        query: String = "",
        source: List<AccessibleAlert> = alerts
    ): List<AccessibleAlert> {
        val predicate = buildAlertPredicate(
            category = category,
            favoriteIds = favoriteIds,
            favoritesOnly = favoritesOnly,
            query = query
        )

        return source
            .filter(predicate)
            .sortedBy { alert -> alert.title }
    }

    fun categoryDescription(category: AlertCategory?): String {
        if (category == null) return "Cinco alertas accesibles disponibles."
        return categoryDescriptions[category].orEmpty()
    }

    fun search(query: String): List<AccessibleAlert> {
        return filterAlerts(
            category = null,
            favoriteIds = emptySet(),
            favoritesOnly = false,
            query = query
        )
    }

    fun findById(id: Int?): AccessibleAlert? {
        return id?.let { alertId -> alerts.firstOrNull { it.id == alertId } }
    }
}
