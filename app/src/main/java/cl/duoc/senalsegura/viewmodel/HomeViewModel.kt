package cl.duoc.senalsegura.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import cl.duoc.senalsegura.data.QuickPhraseRepository
import cl.duoc.senalsegura.model.AccessibleAlert
import cl.duoc.senalsegura.model.MessageAnalysis
import cl.duoc.senalsegura.model.QuickPhrase
import cl.duoc.senalsegura.util.executeAccessibleAction
import cl.duoc.senalsegura.util.MessageAnalyzer
import cl.duoc.senalsegura.util.toAccessibleMessage

data class HomeUiState(
    val writtenMessage: String = "",
    val displayedMessage: String = "Escribe un mensaje o selecciona una frase rápida",
    val selectedCategory: String = "Todas",
    val searchText: String = "",
    val statusMessage: String = "",
    val hasValidationError: Boolean = false,
    val validationAttempts: Int = 0
)

class HomeViewModel : ViewModel() {
    var uiState by mutableStateOf(HomeUiState())
        private set

    val visiblePhrases: List<QuickPhrase>
        get() = QuickPhraseRepository.filterPhrases(
            category = uiState.selectedCategory,
            searchText = uiState.searchText
        )

    val messageAnalysis: MessageAnalysis
        get() = MessageAnalyzer.analyze(uiState.displayedMessage)

    fun updateWrittenMessage(message: String) {
        uiState = uiState.copy(
            writtenMessage = message,
            statusMessage = "",
            hasValidationError = false
        )
    }

    fun showWrittenMessage(): Boolean {
        val wasUpdated = executeAccessibleAction(
            action = {
                val accessibleMessage = uiState.writtenMessage.toAccessibleMessage()
                uiState = uiState.copy(
                    writtenMessage = accessibleMessage,
                    displayedMessage = accessibleMessage,
                    statusMessage = "Mensaje actualizado",
                    hasValidationError = false
                )
                true
            },
            onError = { error ->
                uiState = uiState.copy(
                    statusMessage = error.message.orEmpty(),
                    hasValidationError = true
                )
            },
            onFinally = {
                uiState = uiState.copy(
                    validationAttempts = uiState.validationAttempts + 1
                )
            }
        )

        return wasUpdated ?: false
    }

    fun selectPhrase(phrase: QuickPhrase) {
        uiState = uiState.copy(
            writtenMessage = phrase.text,
            displayedMessage = phrase.text,
            statusMessage = "Frase rápida seleccionada",
            hasValidationError = false
        )
    }

    fun selectAlert(alert: AccessibleAlert) {
        val alertMessage = alert.textForSpeech()
        uiState = uiState.copy(
            writtenMessage = alertMessage,
            displayedMessage = alertMessage,
            statusMessage = "Alerta accesible seleccionada",
            hasValidationError = false
        )
    }

    fun selectCategory(category: String) {
        uiState = uiState.copy(selectedCategory = category)
    }

    fun updateSearchText(searchText: String) {
        uiState = uiState.copy(searchText = searchText)
    }

    fun updateStatus(message: String) {
        uiState = uiState.copy(statusMessage = message, hasValidationError = false)
    }

    fun reset() {
        uiState = HomeUiState()
    }
}
