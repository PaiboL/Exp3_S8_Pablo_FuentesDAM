package cl.duoc.senalsegura.util

import cl.duoc.senalsegura.model.AccessibleAlert
import cl.duoc.senalsegura.model.AlertCategory

class InvalidAccessibleMessageException(message: String) : IllegalArgumentException(message)

/** Propiedad de extensión usada por el análisis y por las pruebas de mensajes. */
val String.wordCount: Int
    get() = trim()
        .split(Regex("\\s+"))
        .count { word -> word.isNotBlank() }

/** Función de extensión que normaliza y valida el texto antes de mostrarlo. */
fun String.toAccessibleMessage(maxLength: Int = 180): String {
    val normalizedMessage = trim().replace(Regex("\\s+"), " ")

    if (normalizedMessage.isBlank()) {
        throw InvalidAccessibleMessageException("Escribe un mensaje antes de mostrarlo")
    }
    if (normalizedMessage.length > maxLength) {
        throw InvalidAccessibleMessageException(
            "El mensaje no puede superar $maxLength caracteres"
        )
    }

    return normalizedMessage
}

/** Propiedad calculada que entrega a TalkBack un resumen completo de cada alerta. */
val AccessibleAlert.accessibilitySummary: String
    get() = "$symbol. $title. ${visibleMessage()}. Categoría ${category.label}"

/**
 * Función de orden superior: construye y devuelve una lambda que combina todos los filtros.
 */
fun buildAlertPredicate(
    category: AlertCategory?,
    favoriteIds: Set<Int>,
    favoritesOnly: Boolean,
    query: String
): (AccessibleAlert) -> Boolean {
    val normalizedQuery = query.trim().lowercase()

    return { alert ->
        val matchesCategory = category == null || alert.category == category
        val matchesFavorite = !favoritesOnly || alert.id in favoriteIds
        val matchesQuery = normalizedQuery.isBlank() ||
            alert.searchableText().contains(normalizedQuery)

        matchesCategory && matchesFavorite && matchesQuery
    }
}

/**
 * Función inline que recibe lambdas para ejecutar una acción, tratar el error esperado y
 * registrar el cierre del proceso. Las excepciones no relacionadas continúan propagándose.
 */
inline fun <T> executeAccessibleAction(
    action: () -> T,
    onError: (InvalidAccessibleMessageException) -> Unit,
    onFinally: () -> Unit
): T? {
    return try {
        action()
    } catch (error: InvalidAccessibleMessageException) {
        onError(error)
        null
    } finally {
        onFinally()
    }
}
