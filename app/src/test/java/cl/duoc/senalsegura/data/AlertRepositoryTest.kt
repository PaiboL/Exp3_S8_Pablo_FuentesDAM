package cl.duoc.senalsegura.data

import cl.duoc.senalsegura.model.AlertCategory
import cl.duoc.senalsegura.model.EmergencyAlert
import cl.duoc.senalsegura.model.InformationAlert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AlertRepositoryTest {

    @Test
    fun alertArrayContainsFiveUniqueObjects() {
        val alerts = AlertRepository.alerts
        val uniqueIds = alerts.map { it.id }.toSet()

        assertEquals(5, alerts.size)
        assertEquals(5, uniqueIds.size)
    }

    @Test
    fun favoritesAndCategoryCanBeCombined() {
        val results = AlertRepository.filterAlerts(
            category = AlertCategory.SAFETY,
            favoriteIds = setOf(2, 3),
            favoritesOnly = true
        )

        assertEquals(1, results.size)
        assertEquals(2, results.first().id)
    }

    @Test
    fun categoryFavoritesAndQueryAreCombinedByReturnedLambda() {
        val results = AlertRepository.filterAlerts(
            category = AlertCategory.SAFETY,
            favoriteIds = setOf(2, 5),
            favoritesOnly = true,
            query = "protección"
        )

        assertEquals(1, results.size)
        assertEquals(5, results.first().id)
    }

    @Test
    fun emergencySubclassAddsSuggestedAction() {
        val alert = AlertRepository.alerts.first() as EmergencyAlert

        assertTrue(alert.textForSpeech().contains("Llame a emergencias"))
    }

    @Test
    fun searchUsesTheObjectProperties() {
        val results = AlertRepository.search("protección")

        assertEquals(1, results.size)
        assertEquals(5, results.first().id)
    }

    @Test
    fun findByIdReturnsTheRequestedAlertAndNullForUnknownId() {
        assertEquals("Evacuación", AlertRepository.findById(2)?.title)
        assertNull(AlertRepository.findById(999))
        assertNull(AlertRepository.findById(null))
    }

    @Test
    fun filterCanUseAlertsObtainedFromAContentProvider() {
        val providerAlerts = listOf(
            InformationAlert(
                id = 20,
                title = "Mensaje del proveedor",
                message = "Contenido consultado mediante URI",
                category = AlertCategory.GENERAL,
                symbol = "i"
            )
        )

        val results = AlertRepository.filterAlerts(
            category = AlertCategory.GENERAL,
            favoriteIds = emptySet(),
            favoritesOnly = false,
            query = "proveedor",
            source = providerAlerts
        )

        assertEquals(listOf(20), results.map { it.id })
    }
}
