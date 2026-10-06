package cl.duoc.senalsegura.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import cl.duoc.senalsegura.data.AlertProviderClient
import cl.duoc.senalsegura.data.AlertRepository
import cl.duoc.senalsegura.model.AccessibleAlert
import cl.duoc.senalsegura.model.AlertCategory

data class AlertsUiState(
    val selectedCategory: AlertCategory? = null,
    val favoritesOnly: Boolean = false,
    val searchText: String = "",
    val favoriteIds: Set<Int> = emptySet(),
    val useCounts: Map<Int, Int> = emptyMap(),
    val providerMessage: String = "Alertas locales disponibles"
)

class AlertsViewModel : ViewModel() {
    var uiState by mutableStateOf(AlertsUiState())
        private set

    private var providerAlerts by mutableStateOf<List<AccessibleAlert>>(emptyList())

    private val alertSource: List<AccessibleAlert>
        get() = providerAlerts.ifEmpty { AlertRepository.alerts }

    val visibleAlerts: List<AccessibleAlert>
        get() = AlertRepository.filterAlerts(
            category = uiState.selectedCategory,
            favoriteIds = uiState.favoriteIds,
            favoritesOnly = uiState.favoritesOnly,
            query = uiState.searchText,
            source = alertSource
        )

    fun loadFromContentProvider(context: Context) {
        AlertProviderClient.queryAlerts(context.applicationContext)
            .onSuccess { alerts ->
                providerAlerts = alerts
                uiState = uiState.copy(
                    providerMessage = "${alerts.size} alertas consultadas mediante ContentProvider"
                )
            }
            .onFailure {
                providerAlerts = emptyList()
                uiState = uiState.copy(
                    providerMessage = "Se utilizaron los datos locales de respaldo"
                )
            }
    }

    fun selectCategory(category: AlertCategory?) {
        uiState = uiState.copy(selectedCategory = category)
    }

    fun setFavoritesOnly(enabled: Boolean) {
        uiState = uiState.copy(favoritesOnly = enabled)
    }

    fun updateSearchText(searchText: String) {
        uiState = uiState.copy(searchText = searchText)
    }

    fun toggleFavorite(alertId: Int) {
        val updatedFavorites = uiState.favoriteIds.toMutableSet()

        if (!updatedFavorites.add(alertId)) {
            updatedFavorites.remove(alertId)
        }

        uiState = uiState.copy(favoriteIds = updatedFavorites.toSet())
    }

    fun registerUse(alertId: Int) {
        val currentCount = uiState.useCounts[alertId] ?: 0
        val updatedCounts = uiState.useCounts + (alertId to (currentCount + 1))
        uiState = uiState.copy(useCounts = updatedCounts)
    }

    fun useCount(alertId: Int): Int = uiState.useCounts[alertId] ?: 0

    fun isFavorite(alertId: Int): Boolean = alertId in uiState.favoriteIds

    fun reset() {
        providerAlerts = emptyList()
        uiState = AlertsUiState()
    }
}
