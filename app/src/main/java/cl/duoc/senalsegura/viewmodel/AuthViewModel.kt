package cl.duoc.senalsegura.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import cl.duoc.senalsegura.data.firebase.AuthSession
import cl.duoc.senalsegura.data.firebase.AuthenticationOutcome
import cl.duoc.senalsegura.data.firebase.FirebaseAuthRepository
import cl.duoc.senalsegura.model.User

data class AuthUiState(
    val isLoading: Boolean = false,
    val message: String = "",
    val isError: Boolean = false
)

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = FirebaseAuthRepository(application)

    var uiState by mutableStateOf(AuthUiState())
        private set

    val isFirebaseConfigured: Boolean
        get() = repository.isFirebaseConfigured

    val connectionLabel: String
        get() = repository.connectionLabel

    fun login(email: String, password: String, onSuccess: (AuthSession) -> Unit) {
        uiState = AuthUiState(isLoading = true)
        repository.login(email, password) { outcome ->
            consumeOutcome(outcome)
            outcome.session?.let(onSuccess)
        }
    }

    fun register(user: User, onSuccess: () -> Unit) {
        uiState = AuthUiState(isLoading = true)
        repository.register(user) { outcome ->
            consumeOutcome(outcome)
            if (!outcome.isError) onSuccess()
        }
    }

    fun sendPasswordReset(email: String) {
        uiState = AuthUiState(isLoading = true)
        repository.sendPasswordReset(email, ::consumeOutcome)
    }

    fun clearMessage() {
        uiState = AuthUiState()
    }

    fun signOut() {
        repository.signOut()
        uiState = AuthUiState()
    }

    private fun consumeOutcome(outcome: AuthenticationOutcome) {
        uiState = AuthUiState(
            isLoading = false,
            message = outcome.message,
            isError = outcome.isError
        )
    }
}
