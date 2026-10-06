package cl.duoc.senalsegura.navigation

sealed class Destination(val route: String) {
    data object Login : Destination("login")
    data object Register : Destination("register")
    data object Recovery : Destination("recovery")
    data object Home : Destination("home")
    data object Alerts : Destination("alerts")
    data object Backend : Destination("backend")
    data object AlertDetail : Destination("alert/{alertId}") {
        const val ARG_ALERT_ID = "alertId"

        fun createRoute(alertId: Int): String = "alert/$alertId"
    }
}
