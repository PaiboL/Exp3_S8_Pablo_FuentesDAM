package cl.duoc.senalsegura

import android.app.Application
import cl.duoc.senalsegura.data.firebase.FirebaseBootstrap

class SignalSafeApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseBootstrap.initialize(this)
    }
}
