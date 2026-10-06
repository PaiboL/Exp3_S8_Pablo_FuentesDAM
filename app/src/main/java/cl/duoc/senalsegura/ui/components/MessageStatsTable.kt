package cl.duoc.senalsegura.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cl.duoc.senalsegura.model.MessageAnalysis

@Composable
fun MessageStatsTable(analysis: MessageAnalysis) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column {
            Text(
                text = "Resumen del mensaje",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )
            HorizontalDivider()
            Row(modifier = Modifier.fillMaxWidth()) {
                TableCell(text = "Prioridad", isHeader = true)
                TableCell(text = "Palabras", isHeader = true)
                TableCell(text = "Lectura", isHeader = true)
            }
            HorizontalDivider()
            Row(modifier = Modifier.fillMaxWidth()) {
                TableCell(text = analysis.priority.label)
                TableCell(text = analysis.wordCount.toString())
                TableCell(text = "${analysis.readingSeconds} s")
            }
        }
    }
}

@Composable
private fun RowScope.TableCell(text: String, isHeader: Boolean = false) {
    Text(
        text = text,
        textAlign = TextAlign.Center,
        fontWeight = if (isHeader) FontWeight.Bold else FontWeight.Normal,
        modifier = Modifier
            .weight(1f)
            .padding(horizontal = 6.dp, vertical = 12.dp)
    )
}
