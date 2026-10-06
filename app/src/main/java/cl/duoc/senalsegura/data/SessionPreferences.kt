package cl.duoc.senalsegura.data

import android.content.Context
import androidx.core.content.edit

class SessionPreferences(context: Context) {
    private val preferences = context.applicationContext.getSharedPreferences(
        FILE_NAME,
        Context.MODE_PRIVATE
    )

    val userName: String
        get() = preferences.getString(KEY_USER_NAME, "").orEmpty()

    val userEmail: String
        get() = preferences.getString(KEY_USER_EMAIL, "").orEmpty()

    val usesFirebase: Boolean
        get() = preferences.getBoolean(KEY_USES_FIREBASE, false)

    var highContrast: Boolean
        get() = preferences.getBoolean(KEY_HIGH_CONTRAST, false)
        set(value) = preferences.edit { putBoolean(KEY_HIGH_CONTRAST, value) }

    fun saveSession(userName: String, userEmail: String, usesFirebase: Boolean) {
        preferences.edit {
            putString(KEY_USER_NAME, userName)
            putString(KEY_USER_EMAIL, userEmail)
            putBoolean(KEY_USES_FIREBASE, usesFirebase)
        }
    }

    fun clearSession() {
        preferences.edit {
            remove(KEY_USER_NAME)
            remove(KEY_USER_EMAIL)
            remove(KEY_USES_FIREBASE)
        }
    }

    private companion object {
        const val FILE_NAME = "senal_segura_preferences"
        const val KEY_USER_NAME = "user_name"
        const val KEY_USER_EMAIL = "user_email"
        const val KEY_USES_FIREBASE = "uses_firebase"
        const val KEY_HIGH_CONTRAST = "high_contrast"
    }
}
