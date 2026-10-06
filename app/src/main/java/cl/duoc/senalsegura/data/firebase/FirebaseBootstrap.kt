package cl.duoc.senalsegura.data.firebase

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseException
import com.google.firebase.database.FirebaseDatabase

object FirebaseBootstrap {
    @Volatile
    private var initialized = false

    @Volatile
    private var configured = false

    fun initialize(context: Context): Boolean {
        if (initialized) return configured

        return synchronized(this) {
            if (!initialized) {
                val app = FirebaseApp.getApps(context).firstOrNull()
                    ?: FirebaseApp.initializeApp(context)

                configured = app != null
                if (configured) {
                    try {
                        FirebaseDatabase.getInstance(app!!).setPersistenceEnabled(true)
                    } catch (_: DatabaseException) {
                        // La persistencia ya fue configurada por otra instancia.
                    }
                }
                initialized = true
            }
            configured
        }
    }

    fun isConfigured(context: Context): Boolean = initialize(context.applicationContext)

    fun authOrNull(context: Context): FirebaseAuth? {
        return if (isConfigured(context)) FirebaseAuth.getInstance() else null
    }

    fun databaseOrNull(context: Context): FirebaseDatabase? {
        return if (isConfigured(context)) FirebaseDatabase.getInstance() else null
    }

    fun connectionLabel(context: Context): String {
        return if (isConfigured(context)) {
            "Firebase configurado: autenticación y sincronización disponibles"
        } else {
            "Modo local de respaldo: agrega app/google-services.json para conectar Firebase"
        }
    }
}
