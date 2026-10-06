package cl.duoc.senalsegura.data.firebase

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class FirebaseUserProfile(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val profile: String = "Persona usuaria",
    val communicationPreference: String = "Texto grande",
    val updatedAt: Long = 0L
) {
    fun normalized(): FirebaseUserProfile = copy(
        name = name.trim(),
        email = email.trim().lowercase(),
        profile = profile.trim(),
        communicationPreference = communicationPreference.trim()
    )

    fun validationError(): String? = when {
        name.isBlank() -> "El nombre del perfil es obligatorio"
        email.isBlank() -> "El correo del perfil es obligatorio"
        else -> null
    }
}

@IgnoreExtraProperties
data class FirebaseAlertRecord(
    val id: String = "",
    val ownerId: String = "",
    val title: String = "",
    val message: String = "",
    val category: String = "General",
    val updatedAt: Long = 0L
) {
    fun normalized(): FirebaseAlertRecord = copy(
        title = title.trim(),
        message = message.trim(),
        category = category.trim().ifBlank { "General" }
    )

    fun validationError(): String? = when {
        title.isBlank() -> "El título es obligatorio"
        message.isBlank() -> "El mensaje es obligatorio"
        title.length > 60 -> "El título no puede superar 60 caracteres"
        message.length > 180 -> "El mensaje no puede superar 180 caracteres"
        else -> null
    }
}

data class AuthSession(
    val userName: String,
    val email: String,
    val usesFirebase: Boolean
)

data class AuthenticationOutcome(
    val session: AuthSession? = null,
    val message: String,
    val isError: Boolean = false
)
