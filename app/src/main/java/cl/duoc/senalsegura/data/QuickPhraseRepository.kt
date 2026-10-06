package cl.duoc.senalsegura.data

import cl.duoc.senalsegura.model.QuickPhrase

object QuickPhraseRepository {
    val phrases: List<QuickPhrase> = listOf(
        QuickPhrase("👋", "Hola, necesito comunicarme por escrito.", "General"),
        QuickPhrase("⏳", "Por favor, espere un momento.", "General"),
        QuickPhrase("✅", "Entendido, muchas gracias.", "General"),
        QuickPhrase("⚠", "¡Peligro! Detenga la operación.", "Seguridad"),
        QuickPhrase("🚪", "Debemos evacuar por esta salida.", "Seguridad"),
        QuickPhrase("🦺", "Necesito mi equipo de protección.", "Seguridad"),
        QuickPhrase("🆘", "Necesito ayuda urgente.", "Salud"),
        QuickPhrase("🏥", "Llame a emergencias, por favor.", "Salud")
    )

    val categories: List<String> = listOf("Todas") +
        phrases.map { it.category }.distinct()

    fun filterPhrases(category: String, searchText: String): List<QuickPhrase> {
        val normalizedSearch = searchText.trim().lowercase()
        val filteredPhrases = mutableListOf<QuickPhrase>()

        for (phrase in phrases) {
            val categoryMatches = category == "Todas" || phrase.category == category
            val searchMatches = normalizedSearch.isBlank() ||
                phrase.text.lowercase().contains(normalizedSearch)

            if (categoryMatches && searchMatches) {
                filteredPhrases.add(phrase)
            }
        }

        return filteredPhrases
    }
}
