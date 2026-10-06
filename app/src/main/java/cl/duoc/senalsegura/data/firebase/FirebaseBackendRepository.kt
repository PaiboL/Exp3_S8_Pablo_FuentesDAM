package cl.duoc.senalsegura.data.firebase

import android.content.Context
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class FirebaseBackendRepository(context: Context) {
    private val appContext = context.applicationContext
    private val localAlerts = defaultAlerts().toMutableList()
    private var localAlertObserver: ((Result<List<FirebaseAlertRecord>>) -> Unit)? = null
    private var localProfile = FirebaseUserProfile()

    val isFirebaseConfigured: Boolean
        get() = FirebaseBootstrap.isConfigured(appContext)

    val connectionLabel: String
        get() = FirebaseBootstrap.connectionLabel(appContext)

    fun observeAlerts(
        onResult: (Result<List<FirebaseAlertRecord>>) -> Unit
    ): () -> Unit {
        val userId = currentUserId()
        val reference = alertsReference(userId)

        if (reference == null) {
            localAlertObserver = onResult
            onResult(Result.success(localAlerts.toList()))
            return { localAlertObserver = null }
        }

        var seeded = false
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val records = snapshot.children.mapNotNull { child ->
                    child.getValue(FirebaseAlertRecord::class.java)
                        ?.copy(id = child.key.orEmpty())
                }.sortedBy { record -> record.title.lowercase() }

                if (records.isEmpty() && !seeded) {
                    seeded = true
                    seedDefaults(referenceUserId = userId.orEmpty())
                }
                onResult(Result.success(records))
            }

            override fun onCancelled(error: DatabaseError) {
                onResult(Result.failure(error.toException()))
            }
        }
        reference.addValueEventListener(listener)
        return { reference.removeEventListener(listener) }
    }

    fun observeProfile(
        fallbackName: String,
        fallbackEmail: String,
        onResult: (Result<FirebaseUserProfile>) -> Unit
    ): () -> Unit {
        val userId = currentUserId()
        val reference = profileReference(userId)

        if (reference == null) {
            if (localProfile.uid.isBlank()) {
                localProfile = FirebaseUserProfile(
                    uid = "local-user",
                    name = fallbackName,
                    email = fallbackEmail,
                    updatedAt = System.currentTimeMillis()
                )
            }
            onResult(Result.success(localProfile))
            return {}
        }

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val profile = snapshot.getValue(FirebaseUserProfile::class.java)
                    ?: FirebaseUserProfile(
                        uid = userId.orEmpty(),
                        name = "",
                        email = fallbackEmail,
                        updatedAt = System.currentTimeMillis()
                    )
                onResult(Result.success(profile))
            }

            override fun onCancelled(error: DatabaseError) {
                onResult(Result.failure(error.toException()))
            }
        }
        reference.addValueEventListener(listener)
        return { reference.removeEventListener(listener) }
    }

    fun createAlert(
        title: String,
        message: String,
        category: String,
        onResult: (Result<Unit>) -> Unit
    ) {
        val userId = currentUserId()
        val reference = alertsReference(userId)?.push()

        if (reference == null || userId == null) {
            localAlerts += FirebaseAlertRecord(
                id = "local-${System.currentTimeMillis()}",
                ownerId = "local-user",
                title = title,
                message = message,
                category = category,
                updatedAt = System.currentTimeMillis()
            ).normalized()
            notifyLocalAlerts()
            onResult(Result.success(Unit))
            return
        }

        val key = reference.key.orEmpty()
        val record = FirebaseAlertRecord(
            id = key,
            ownerId = userId,
            title = title,
            message = message,
            category = category,
            updatedAt = System.currentTimeMillis()
        ).normalized()

        reference.setValue(record)
            .addOnSuccessListener { onResult(Result.success(Unit)) }
            .addOnFailureListener { error -> onResult(Result.failure(error)) }
    }

    fun updateAlert(record: FirebaseAlertRecord, onResult: (Result<Unit>) -> Unit) {
        val userId = currentUserId()
        val reference = alertsReference(userId)?.child(record.id)

        val updated = record.copy(
            ownerId = userId ?: "local-user",
            updatedAt = System.currentTimeMillis()
        ).normalized()

        if (reference == null) {
            val index = localAlerts.indexOfFirst { it.id == record.id }
            if (index >= 0) {
                localAlerts[index] = updated
                notifyLocalAlerts()
                onResult(Result.success(Unit))
            } else {
                onResult(Result.failure(IllegalArgumentException("La alerta no existe")))
            }
            return
        }

        val changes = mapOf<String, Any>(
            "title" to updated.title,
            "message" to updated.message,
            "category" to updated.category,
            "ownerId" to updated.ownerId,
            "updatedAt" to updated.updatedAt
        )
        reference.updateChildren(changes)
            .addOnSuccessListener { onResult(Result.success(Unit)) }
            .addOnFailureListener { error -> onResult(Result.failure(error)) }
    }

    fun deleteAlert(recordId: String, onResult: (Result<Unit>) -> Unit) {
        val userId = currentUserId()
        val reference = alertsReference(userId)?.child(recordId)

        if (reference == null) {
            localAlerts.removeAll { it.id == recordId }
            notifyLocalAlerts()
            onResult(Result.success(Unit))
            return
        }

        reference.removeValue()
            .addOnSuccessListener { onResult(Result.success(Unit)) }
            .addOnFailureListener { error -> onResult(Result.failure(error)) }
    }

    fun saveProfile(profile: FirebaseUserProfile, onResult: (Result<Unit>) -> Unit) {
        val userId = currentUserId()
        val normalized = profile.copy(
            uid = userId ?: "local-user",
            updatedAt = System.currentTimeMillis()
        ).normalized()
        val reference = profileReference(userId)

        if (reference == null) {
            localProfile = normalized
            onResult(Result.success(Unit))
            return
        }

        reference.setValue(normalized)
            .addOnSuccessListener { onResult(Result.success(Unit)) }
            .addOnFailureListener { error -> onResult(Result.failure(error)) }
    }

    fun deleteProfile(onResult: (Result<Unit>) -> Unit) {
        val reference = profileReference()

        if (reference == null) {
            localProfile = FirebaseUserProfile()
            onResult(Result.success(Unit))
            return
        }

        reference.removeValue()
            .addOnSuccessListener { onResult(Result.success(Unit)) }
            .addOnFailureListener { error -> onResult(Result.failure(error)) }
    }

    private fun seedDefaults(referenceUserId: String) {
        val userReference = alertsReference(referenceUserId) ?: return

        defaultAlerts().forEach { default ->
            val child = userReference.push()
            val key = child.key.orEmpty()
            child.setValue(
                default.copy(
                    id = key,
                    ownerId = referenceUserId,
                    updatedAt = System.currentTimeMillis()
                )
            )
        }
    }

    private fun currentUserId(): String? =
        FirebaseBootstrap.authOrNull(appContext)?.currentUser?.uid

    private fun alertsReference(uid: String? = currentUserId()): DatabaseReference? {
        if (uid.isNullOrBlank()) return null
        return FirebaseBootstrap.databaseOrNull(appContext)
            ?.getReference("alerts")
            ?.child(uid)
    }

    private fun profileReference(uid: String? = currentUserId()): DatabaseReference? {
        if (uid.isNullOrBlank()) return null
        return FirebaseBootstrap.databaseOrNull(appContext)
            ?.getReference("users")
            ?.child(uid)
    }

    private fun notifyLocalAlerts() {
        localAlertObserver?.invoke(
            Result.success(localAlerts.sortedBy { record -> record.title.lowercase() })
        )
    }

    companion object {
        fun defaultAlerts(): List<FirebaseAlertRecord> = listOf(
            FirebaseAlertRecord(
                id = "demo-1",
                title = "Necesito ayuda urgente",
                message = "Necesito ayuda inmediata",
                category = "Salud"
            ),
            FirebaseAlertRecord(
                id = "demo-2",
                title = "Evacuación",
                message = "Debemos evacuar por la salida señalizada",
                category = "Seguridad"
            ),
            FirebaseAlertRecord(
                id = "demo-3",
                title = "Comunicación escrita",
                message = "Por favor, comuníquese conmigo por escrito",
                category = "General"
            )
        )
    }
}
