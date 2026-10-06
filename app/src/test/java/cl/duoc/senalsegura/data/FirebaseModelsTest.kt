package cl.duoc.senalsegura.data

import cl.duoc.senalsegura.data.firebase.FirebaseAlertRecord
import cl.duoc.senalsegura.data.firebase.FirebaseBackendRepository
import cl.duoc.senalsegura.data.firebase.FirebaseUserProfile
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FirebaseModelsTest {

    @Test
    fun alertRecordNormalizesFieldsAndAcceptsValidData() {
        val record = FirebaseAlertRecord(
            title = "  Evacuación  ",
            message = "  Salga por la puerta señalizada  ",
            category = "  Seguridad  "
        ).normalized()

        assertEquals("Evacuación", record.title)
        assertEquals("Salga por la puerta señalizada", record.message)
        assertEquals("Seguridad", record.category)
        assertNull(record.validationError())
    }

    @Test
    fun alertRecordRejectsEmptyMessage() {
        val error = FirebaseAlertRecord(
            title = "Alerta",
            message = ""
        ).validationError()

        assertNotNull(error)
    }

    @Test
    fun userProfileNormalizesEmail() {
        val profile = FirebaseUserProfile(
            name = " Pablo Fuentes ",
            email = " PABLO@DUOC.CL "
        ).normalized()

        assertEquals("Pablo Fuentes", profile.name)
        assertEquals("pablo@duoc.cl", profile.email)
        assertNull(profile.validationError())
    }

    @Test
    fun defaultFirebaseRecordsHaveUniqueIdentifiers() {
        val records = FirebaseBackendRepository.defaultAlerts()
        assertTrue(records.isNotEmpty())
        assertEquals(records.size, records.map { record -> record.id }.distinct().size)
    }
}
