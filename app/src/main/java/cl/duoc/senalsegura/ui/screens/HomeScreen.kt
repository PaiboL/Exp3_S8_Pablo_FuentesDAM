package cl.duoc.senalsegura.ui.screens

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.senalsegura.data.QuickPhraseRepository
import cl.duoc.senalsegura.model.QuickPhrase
import cl.duoc.senalsegura.ui.components.AppTopBar
import cl.duoc.senalsegura.ui.components.MessageStatsTable
import cl.duoc.senalsegura.viewmodel.HomeViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    userName: String,
    highContrast: Boolean,
    onHighContrastChange: (Boolean) -> Unit,
    onOpenAlerts: () -> Unit,
    onOpenBackend: () -> Unit,
    onLogout: () -> Unit
) {
    var categoryMenuOpen by rememberSaveable { mutableStateOf(false) }
    var visualAlertOpen by rememberSaveable { mutableStateOf(false) }

    val uiState = viewModel.uiState
    val visiblePhrases = viewModel.visiblePhrases
    val messageAnalysis = viewModel.messageAnalysis

    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    var ttsReady by remember { mutableStateOf(false) }
    val textToSpeech = remember {
        TextToSpeech(context) { status ->
            ttsReady = status == TextToSpeech.SUCCESS
        }
    }

    LaunchedEffect(ttsReady) {
        if (ttsReady) {
            textToSpeech.language = Locale("es", "CL")
        }
    }

    DisposableEffect(textToSpeech) {
        onDispose {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
    }

    if (visualAlertOpen) {
        AlertDialog(
            onDismissRequest = { visualAlertOpen = false },
            confirmButton = {
                Button(onClick = { visualAlertOpen = false }) {
                    Text("Entendido")
                }
            },
            title = {
                Text(
                    text = "⚠ ALERTA VISUAL",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black
                )
            },
            text = {
                Text(
                    text = uiState.displayedMessage,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "SeñalSegura",
                actionLabel = "Salir",
                onAction = onLogout
            )
        }
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 170.dp),
            modifier = Modifier.padding(innerPadding),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column {
                    Text(
                        text = "Hola, $userName",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text("Comunica tu mensaje de forma visible o mediante voz sintetizada.")
                }
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(22.dp)
                    ) {
                        Text(
                            text = "MENSAJE PARA MOSTRAR",
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(10.dp))
                        Text(
                            text = uiState.displayedMessage,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontSize = 28.sp,
                            lineHeight = 36.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                MessageStatsTable(analysis = messageAnalysis)
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                OutlinedTextField(
                    value = uiState.writtenMessage,
                    onValueChange = viewModel::updateWrittenMessage,
                    label = { Text("Escribe aquí tu mensaje") },
                    supportingText = {
                        Text("${uiState.writtenMessage.length}/180 caracteres")
                    },
                    isError = uiState.writtenMessage.length > 180,
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            if (viewModel.showWrittenMessage()) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            }
                        },
                        enabled = uiState.writtenMessage.isNotBlank(),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Mostrar")
                    }
                    OutlinedButton(
                        onClick = {
                            if (ttsReady && uiState.displayedMessage.isNotBlank()) {
                                textToSpeech.speak(
                                    uiState.displayedMessage,
                                    TextToSpeech.QUEUE_FLUSH,
                                    null,
                                    "senal_segura_message"
                                )
                                viewModel.updateStatus("Reproduciendo mensaje por voz")
                            } else {
                                viewModel.updateStatus("La voz aún no está disponible")
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Reproducir voz")
                    }
                }
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                Button(
                    onClick = {
                        visualAlertOpen = true
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("⚠ Activar alerta visual")
                }
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                OutlinedButton(
                    onClick = onOpenAlerts,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Ver 5 alertas accesibles")
                }
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                Button(
                    onClick = onOpenBackend,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Administrar datos y Firebase")
                }
            }

            if (uiState.statusMessage.isNotBlank()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text(
                        text = if (uiState.hasValidationError) {
                            "⚠ ${uiState.statusMessage}"
                        } else {
                            "✓ ${uiState.statusMessage}"
                        },
                        color = if (uiState.hasValidationError) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.primary
                        },
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Modo de alto contraste", fontWeight = FontWeight.Bold)
                        Text("Mejora la lectura en ambientes con poca luz.")
                    }
                    Switch(
                        checked = highContrast,
                        onCheckedChange = onHighContrastChange
                    )
                }
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                Text("Frases rápidas", style = MaterialTheme.typography.titleLarge)
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                ExposedDropdownMenuBox(
                    expanded = categoryMenuOpen,
                    onExpandedChange = { categoryMenuOpen = !categoryMenuOpen }
                ) {
                    OutlinedTextField(
                        value = uiState.selectedCategory,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Filtrar por categoría") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryMenuOpen)
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = categoryMenuOpen,
                        onDismissRequest = { categoryMenuOpen = false }
                    ) {
                        QuickPhraseRepository.categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category) },
                                onClick = {
                                    viewModel.selectCategory(category)
                                    categoryMenuOpen = false
                                }
                            )
                        }
                    }
                }
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                OutlinedTextField(
                    value = uiState.searchText,
                    onValueChange = viewModel::updateSearchText,
                    label = { Text("Buscar frase rápida") },
                    supportingText = {
                        Text(
                            "${visiblePhrases.size} de " +
                                "${QuickPhraseRepository.phrases.size} frases disponibles"
                        )
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            items(visiblePhrases) { phrase ->
                QuickPhraseCard(
                    phrase = phrase,
                    onClick = {
                        viewModel.selectPhrase(phrase)
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    }
                )
            }
        }
    }
}

@Composable
private fun QuickPhraseCard(
    phrase: QuickPhrase,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(phrase.symbol, fontSize = 30.sp)
            Spacer(Modifier.height(6.dp))
            Text(
                text = phrase.text,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = phrase.category,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
