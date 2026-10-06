package cl.duoc.senalsegura

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModelProvider
import cl.duoc.senalsegura.data.SessionPreferences
import cl.duoc.senalsegura.navigation.AppNavigation
import cl.duoc.senalsegura.ui.theme.SenalSeguraTheme
import cl.duoc.senalsegura.viewmodel.AlertsViewModel
import cl.duoc.senalsegura.viewmodel.AuthViewModel
import cl.duoc.senalsegura.viewmodel.BackendViewModel
import cl.duoc.senalsegura.viewmodel.HomeViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        val alertsViewModel = ViewModelProvider(this)[AlertsViewModel::class.java]
        val authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        val backendViewModel = ViewModelProvider(this)[BackendViewModel::class.java]

        setContent {
            val sessionPreferences = remember { SessionPreferences(applicationContext) }
            var highContrast by rememberSaveable {
                mutableStateOf(sessionPreferences.highContrast)
            }

            SenalSeguraTheme(highContrast = highContrast) {
                AppNavigation(
                    homeViewModel = homeViewModel,
                    alertsViewModel = alertsViewModel,
                    authViewModel = authViewModel,
                    backendViewModel = backendViewModel,
                    sessionPreferences = sessionPreferences,
                    highContrast = highContrast,
                    onHighContrastChange = { enabled ->
                        highContrast = enabled
                        sessionPreferences.highContrast = enabled
                    }
                )
            }
        }
    }
}
