package cl.duoc.senalsegura.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import cl.duoc.senalsegura.data.SessionPreferences
import cl.duoc.senalsegura.ui.screens.AlertDetailScreen
import cl.duoc.senalsegura.ui.screens.BackendScreen
import cl.duoc.senalsegura.ui.screens.HomeScreen
import cl.duoc.senalsegura.ui.screens.LoginScreen
import cl.duoc.senalsegura.ui.screens.RecoveryScreen
import cl.duoc.senalsegura.ui.screens.RegisterScreen
import cl.duoc.senalsegura.ui.screens.AlertsScreen
import cl.duoc.senalsegura.viewmodel.AlertsViewModel
import cl.duoc.senalsegura.viewmodel.AuthViewModel
import cl.duoc.senalsegura.viewmodel.BackendViewModel
import cl.duoc.senalsegura.viewmodel.HomeViewModel

@Composable
fun AppNavigation(
    homeViewModel: HomeViewModel,
    alertsViewModel: AlertsViewModel,
    authViewModel: AuthViewModel,
    backendViewModel: BackendViewModel,
    sessionPreferences: SessionPreferences,
    highContrast: Boolean,
    onHighContrastChange: (Boolean) -> Unit
) {
    val navController = rememberNavController()
    var currentUserName by rememberSaveable {
        mutableStateOf(sessionPreferences.userName)
    }
    var currentUserEmail by rememberSaveable {
        mutableStateOf(sessionPreferences.userEmail)
    }

    NavHost(
        navController = navController,
        startDestination = Destination.Login.route
    ) {
        composable(Destination.Login.route) {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = { session ->
                    currentUserName = session.userName
                    currentUserEmail = session.email
                    sessionPreferences.saveSession(
                        userName = session.userName,
                        userEmail = session.email,
                        usesFirebase = session.usesFirebase
                    )
                    navController.navigate(Destination.Home.route) {
                        popUpTo(Destination.Login.route) { inclusive = true }
                    }
                },
                onRegister = { navController.navigate(Destination.Register.route) },
                onRecovery = { navController.navigate(Destination.Recovery.route) }
            )
        }

        composable(Destination.Register.route) {
            RegisterScreen(
                viewModel = authViewModel,
                onBack = { navController.popBackStack() },
                onRegistrationSuccess = {
                    navController.navigate(Destination.Login.route) {
                        popUpTo(Destination.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Destination.Recovery.route) {
            RecoveryScreen(
                viewModel = authViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Destination.Home.route) {
            HomeScreen(
                viewModel = homeViewModel,
                userName = currentUserName.ifBlank { "Usuario" },
                highContrast = highContrast,
                onHighContrastChange = onHighContrastChange,
                onOpenAlerts = { navController.navigate(Destination.Alerts.route) },
                onOpenBackend = { navController.navigate(Destination.Backend.route) },
                onLogout = {
                    authViewModel.signOut()
                    backendViewModel.stopObserving()
                    sessionPreferences.clearSession()
                    homeViewModel.reset()
                    alertsViewModel.reset()
                    currentUserName = ""
                    currentUserEmail = ""
                    onHighContrastChange(false)
                    navController.navigate(Destination.Login.route) {
                        popUpTo(Destination.Home.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Destination.Backend.route) {
            BackendScreen(
                viewModel = backendViewModel,
                userName = currentUserName.ifBlank { "Usuario" },
                userEmail = currentUserEmail,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Destination.Alerts.route) {
            AlertsScreen(
                viewModel = alertsViewModel,
                onBack = { navController.popBackStack() },
                onOpenAlert = { alertId ->
                    navController.navigate(Destination.AlertDetail.createRoute(alertId))
                },
                onUseAlert = { alert ->
                    alertsViewModel.registerUse(alert.id)
                    homeViewModel.selectAlert(alert)
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Destination.AlertDetail.route,
            arguments = listOf(
                navArgument(Destination.AlertDetail.ARG_ALERT_ID) {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val alertId = backStackEntry.arguments
                ?.getInt(Destination.AlertDetail.ARG_ALERT_ID)
                ?: -1

            AlertDetailScreen(
                alertId = alertId,
                onBack = { navController.popBackStack() },
                onUseAlert = { alert ->
                    alertsViewModel.registerUse(alert.id)
                    homeViewModel.selectAlert(alert)
                    navController.popBackStack(Destination.Home.route, inclusive = false)
                }
            )
        }
    }
}
