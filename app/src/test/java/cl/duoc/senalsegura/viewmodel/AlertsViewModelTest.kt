package cl.duoc.senalsegura.viewmodel

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AlertsViewModelTest {

    @Test
    fun favoriteSetAvoidsDuplicatesAndAllowsRemoval() {
        val viewModel = AlertsViewModel()

        viewModel.toggleFavorite(1)
        assertTrue(viewModel.isFavorite(1))
        assertEquals(1, viewModel.uiState.favoriteIds.size)

        viewModel.toggleFavorite(1)
        assertFalse(viewModel.isFavorite(1))
    }

    @Test
    fun usageMapAccumulatesSelections() {
        val viewModel = AlertsViewModel()

        viewModel.registerUse(2)
        viewModel.registerUse(2)

        assertEquals(2, viewModel.useCount(2))
    }

    @Test
    fun searchTextFiltersVisibleAlerts() {
        val viewModel = AlertsViewModel()

        viewModel.updateSearchText("evacuación")

        assertEquals(1, viewModel.visibleAlerts.size)
        assertEquals(2, viewModel.visibleAlerts.first().id)
    }
}
