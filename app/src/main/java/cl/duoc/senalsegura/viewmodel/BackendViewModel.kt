package cl.duoc.senalsegura.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import cl.duoc.senalsegura.data.firebase.FirebaseAlertRecord
import cl.duoc.senalsegura.data.firebase.FirebaseBackendRepository
import cl.duoc.senalsegura.data.firebase.FirebaseUserProfile

data class BackendUiState(
    val alerts: List<FirebaseAlertRecord> = emptyList(),
    val title: String = "",
    val message: String = "",
    val category: String = "General",
    val searchText: String = "",
    val editingId: String? = null,
    val profile: FirebaseUserProfile = FirebaseUserProfile(),
    val profileName: String = "",
    val profilePreference: String = "Texto grande",
    val statusMessage: String = "",
    val isError: Boolean = false,
    val isSaving: Boolean = false
)

class BackendViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = FirebaseBackendRepository(application)
    private var stopAlerts: (() -> Unit)? = null
    private var stopProfile: (() -> Unit)? = null
    private var observingEmail: String? = null

    var uiState by mutableStateOf(BackendUiState())
        private set

    val isFirebaseConfigured: Boolean
        get() = repository.isFirebaseConfigured

    val connectionLabel: String
        get() = repository.connectionLabel

    val visibleAlerts: List<FirebaseAlertRecord>
        get() {
            val query = uiState.searchText.trim().lowercase()
            if (query.isBlank()) return uiState.alerts

            return uiState.alerts.filter { record ->
                record.title.lowercase().contains(query) ||
                    record.message.lowercase().contains(query) ||
                    record.category.lowercase().contains(query)
            }
        }

    fun start(userName: String, userEmail: String) {
        if (observingEmail == userEmail && stopAlerts != null) return
        stopObserving()
        observingEmail = userEmail

        stopAlerts = repository.observeAlerts { result ->
            result.onSuccess { alerts ->
                uiState = uiState.copy(alerts = alerts, isError = false)
            }.onFailure { error ->
                uiState = uiState.copy(
                    statusMessage = error.localizedMessage ?: "No fue posible leer las alertas",
                    isError = true
                )
            }
        }

        stopProfile = repository.observeProfile(userName, userEmail) { result ->
            result.onSuccess { profile ->
                uiState = uiState.copy(
                    profile = profile,
                    profileName = profile.name,
                    profilePreference = profile.communicationPreference
                )
            }.onFailure { error ->
                uiState = uiState.copy(
                    statusMessage = error.localizedMessage ?: "No fue posible leer el perfil",
                    isError = true
                )
            }
        }
    }

    fun stopObserving() {
        stopAlerts?.invoke()
        stopProfile?.invoke()
        stopAlerts = null
        stopProfile = null
        observingEmail = null
    }

    fun updateTitle(value: String) {
        uiState = uiState.copy(title = value, statusMessage = "", isError = false)
    }

    fun updateMessage(value: String) {
        uiState = uiState.copy(message = value, statusMessage = "", isError = false)
    }

    fun updateCategory(value: String) {
        uiState = uiState.copy(category = value, statusMessage = "", isError = false)
    }

    fun updateSearchText(value: String) {
        uiState = uiState.copy(searchText = value)
    }

    fun editAlert(record: FirebaseAlertRecord) {
        uiState = uiState.copy(
            title = record.title,
            message = record.message,
            category = record.category,
            editingId = record.id,
            statusMessage = "Editando ${record.title}",
            isError = false
        )
    }

    fun cancelEdit() {
        clearAlertForm()
    }

    fun saveAlert() {
        val record = FirebaseAlertRecord(
            id = uiState.editingId.orEmpty(),
            title = uiState.title,
            message = uiState.message,
            category = uiState.category
        ).normalized()
        val error = record.validationError()
        if (error != null) {
            uiState = uiState.copy(statusMessage = error, isError = true)
            return
        }

        uiState = uiState.copy(isSaving = true, statusMessage = "")
        val callback: (Result<Unit>) -> Unit = { result ->
            result.onSuccess {
                val action = if (record.id.isBlank()) "creada" else "actualizada"
                clearAlertForm(status = "Alerta $action correctamente")
            }.onFailure { failure ->
                uiState = uiState.copy(
                    isSaving = false,
                    statusMessage = failure.localizedMessage ?: "No fue posible guardar la alerta",
                    isError = true
                )
            }
        }

        if (record.id.isBlank()) {
            repository.createAlert(record.title, record.message, record.category, callback)
        } else {
            repository.updateAlert(record, callback)
        }
    }

    fun deleteAlert(recordId: String) {
        uiState = uiState.copy(isSaving = true, statusMessage = "")
        repository.deleteAlert(recordId) { result ->
            result.onSuccess {
                clearAlertForm(status = "Alerta eliminada correctamente")
            }.onFailure { failure ->
                uiState = uiState.copy(
                    isSaving = false,
                    statusMessage = failure.localizedMessage ?: "No fue posible eliminar la alerta",
                    isError = true
                )
            }
        }
    }

    fun updateProfileName(value: String) {
        uiState = uiState.copy(profileName = value, statusMessage = "", isError = false)
    }

    fun updateProfilePreference(value: String) {
        uiState = uiState.copy(profilePreference = value, statusMessage = "", isError = false)
    }

    fun saveProfile() {
        val profile = uiState.profile.copy(
            name = uiState.profileName,
            communicationPreference = uiState.profilePreference
        ).normalized()
        val error = profile.validationError()
        if (error != null) {
            uiState = uiState.copy(statusMessage = error, isError = true)
            return
        }

        uiState = uiState.copy(isSaving = true, statusMessage = "")
        repository.saveProfile(profile) { result ->
            result.onSuccess {
                uiState = uiState.copy(
                    profile = profile,
                    isSaving = false,
                    statusMessage = "Perfil actualizado correctamente",
                    isError = false
                )
            }.onFailure { failure ->
                uiState = uiState.copy(
                    isSaving = false,
                    statusMessage = failure.localizedMessage ?: "No fue posible actualizar el perfil",
                    isError = true
                )
            }
        }
    }

    fun deleteProfile() {
        uiState = uiState.copy(isSaving = true, statusMessage = "")
        repository.deleteProfile { result ->
            result.onSuccess {
                uiState = uiState.copy(
                    profile = FirebaseUserProfile(email = observingEmail.orEmpty()),
                    profileName = "",
                    isSaving = false,
                    statusMessage = "Perfil eliminado de la base de datos",
                    isError = false
                )
            }.onFailure { failure ->
                uiState = uiState.copy(
                    isSaving = false,
                    statusMessage = failure.localizedMessage ?: "No fue posible eliminar el perfil",
                    isError = true
                )
            }
        }
    }

    override fun onCleared() {
        stopObserving()
        super.onCleared()
    }

    private fun clearAlertForm(status: String = "") {
        uiState = uiState.copy(
            title = "",
            message = "",
            category = "General",
            editingId = null,
            isSaving = false,
            statusMessage = status,
            isError = false
        )
    }
}
