package cl.duoc.senalsegura.viewmodel

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HomeViewModelTest {

    @Test
    fun validMessageIsNormalizedAndDisplayed() {
        val viewModel = HomeViewModel()

        viewModel.updateWrittenMessage("  Necesito   ayuda  ")

        assertTrue(viewModel.showWrittenMessage())
        assertEquals("Necesito ayuda", viewModel.uiState.displayedMessage)
        assertEquals(1, viewModel.uiState.validationAttempts)
    }

    @Test
    fun longMessageIsRejectedAndFinallyStillRuns() {
        val viewModel = HomeViewModel()

        viewModel.updateWrittenMessage("a".repeat(181))

        assertFalse(viewModel.showWrittenMessage())
        assertEquals("Escribe un mensaje o selecciona una frase rápida", viewModel.uiState.displayedMessage)
        assertEquals("El mensaje no puede superar 180 caracteres", viewModel.uiState.statusMessage)
        assertTrue(viewModel.uiState.hasValidationError)
        assertEquals(1, viewModel.uiState.validationAttempts)
    }
}
