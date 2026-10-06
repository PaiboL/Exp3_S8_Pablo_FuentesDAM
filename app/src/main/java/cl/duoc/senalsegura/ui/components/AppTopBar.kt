package cl.duoc.senalsegura.ui.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    onBack: (() -> Unit)? = null,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            if (onBack != null) {
                TextButton(onClick = onBack) {
                    Text(
                        "‹ Volver",
                        color = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        },
        actions = {
            if (actionLabel != null && onAction != null) {
                TextButton(onClick = onAction) {
                    Text(
                        actionLabel,
                        color = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
            titleContentColor = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary
        )
    )
}
