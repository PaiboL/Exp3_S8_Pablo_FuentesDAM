package cl.duoc.senalsegura.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class QuickPhraseRepositoryTest {

    @Test
    fun quickPhrasesContainTheThreeRequiredCategories() {
        val categories = QuickPhraseRepository.phrases.map { it.category }.toSet()

        assertTrue(categories.contains("General"))
        assertTrue(categories.contains("Seguridad"))
        assertTrue(categories.contains("Salud"))
    }

    @Test
    fun quickPhraseCatalogContainsEightOptions() {
        assertEquals(8, QuickPhraseRepository.phrases.size)
    }

    @Test
    fun filterCombinesCategoryAndSearchText() {
        val results = QuickPhraseRepository.filterPhrases(
            category = "Seguridad",
            searchText = "evacuar"
        )

        assertEquals(1, results.size)
        assertEquals("Debemos evacuar por esta salida.", results.first().text)
    }

    @Test
    fun emptySearchAndAllCategoryReturnTheFullCatalog() {
        val results = QuickPhraseRepository.filterPhrases("Todas", "")

        assertEquals(QuickPhraseRepository.phrases, results)
    }
}
