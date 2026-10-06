package cl.duoc.senalsegura.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.senalsegura.R
import cl.duoc.senalsegura.model.AccessibleAlert
import cl.duoc.senalsegura.model.EmergencyAlert
import cl.duoc.senalsegura.util.accessibilitySummary

@Composable
fun PaletteHeader(
    palette: AlertPaletteColors,
    providerMessage: String,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = palette.dominant,
            contentColor = palette.onDominant
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.alert_palette_art),
                contentDescription = "Ilustración accesible utilizada para generar la paleta",
                modifier = Modifier.size(72.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Alertas accesibles",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = providerMessage,
                    color = palette.onDominant.copy(alpha = 0.88f)
                )
                Text(
                    text = "Colores obtenidos con Android Palette",
                    style = MaterialTheme.typography.bodySmall,
                    color = palette.onDominant.copy(alpha = 0.76f)
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AccessibleAlertCard(
    alert: AccessibleAlert,
    isFavorite: Boolean,
    useCount: Int,
    palette: AlertPaletteColors,
    onOpen: () -> Unit,
    onFavoriteChange: () -> Unit,
    onUse: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = palette.muted.copy(alpha = 0.16f)
        ),
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onOpen,
                onLongClick = onUse,
                onLongClickLabel = "Usar alerta"
            )
            .semantics { contentDescription = alert.accessibilitySummary }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = alert.symbol, fontSize = 32.sp)
            Text(
                text = alert.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = palette.vibrant
            )
            Text(
                text = alert.category.label,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(8.dp))
            Text(alert.visibleMessage())

            if (alert is EmergencyAlert) {
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Acción: ${alert.actionDescription()}",
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = isFavorite,
                    onCheckedChange = { onFavoriteChange() }
                )
                Text("Marcar como favorita")
            }
            Text(
                text = "Usada $useCount veces",
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(Modifier.height(10.dp))
            Button(
                onClick = onUse,
                colors = ButtonDefaults.buttonColors(
                    containerColor = palette.vibrant,
                    contentColor = palette.onVibrant
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Usar este mensaje")
            }
            Text(
                text = "Toca la tarjeta para ver el detalle. Mantén presionado para usarla.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
