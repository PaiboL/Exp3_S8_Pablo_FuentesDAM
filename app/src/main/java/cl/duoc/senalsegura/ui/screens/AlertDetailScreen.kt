package cl.duoc.senalsegura.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.senalsegura.R
import cl.duoc.senalsegura.data.AlertRepository
import cl.duoc.senalsegura.model.AccessibleAlert
import cl.duoc.senalsegura.model.EmergencyAlert
import cl.duoc.senalsegura.ui.components.AppTopBar
import cl.duoc.senalsegura.ui.components.PaletteHeader
import cl.duoc.senalsegura.ui.components.rememberAlertPaletteColors

@Composable
fun AlertDetailScreen(
    alertId: Int,
    onBack: () -> Unit,
    onUseAlert: (AccessibleAlert) -> Unit
) {
    val alert = AlertRepository.findById(alertId)
    val palette = rememberAlertPaletteColors(
        drawableResource = R.drawable.alert_palette_art,
        fallbackDominant = MaterialTheme.colorScheme.primaryContainer,
        fallbackVibrant = MaterialTheme.colorScheme.primary,
        fallbackMuted = MaterialTheme.colorScheme.secondaryContainer
    )

    Scaffold(
        topBar = { AppTopBar(title = "Detalle de alerta", onBack = onBack) }
    ) { innerPadding ->
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            PaletteHeader(
                palette = palette,
                providerMessage = "Vista de detalle reutilizable"
            )

            if (alert == null) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "La alerta solicitada no está disponible.",
                        modifier = Modifier.padding(18.dp)
                    )
                }
                OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                    Text("Volver")
                }
                return@Column
            }

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = palette.muted.copy(alpha = 0.16f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(22.dp)) {
                    Text(alert.symbol, fontSize = 54.sp)
                    Text(
                        text = alert.title,
                        style = MaterialTheme.typography.headlineSmall,
                        color = palette.vibrant,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = alert.category.label,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = alert.visibleMessage(),
                        fontSize = 24.sp,
                        lineHeight = 32.sp,
                        fontWeight = FontWeight.Medium
                    )

                    if (alert is EmergencyAlert) {
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = "Acción sugerida",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                        Text(alert.actionDescription())
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Volver")
                }
                Button(
                    onClick = { onUseAlert(alert) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = palette.vibrant,
                        contentColor = palette.onVibrant
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Usar mensaje")
                }
            }
        }
    }
}
