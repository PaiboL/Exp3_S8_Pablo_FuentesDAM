package cl.duoc.senalsegura.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cl.duoc.senalsegura.R
import cl.duoc.senalsegura.data.AlertRepository
import cl.duoc.senalsegura.model.AccessibleAlert
import cl.duoc.senalsegura.ui.components.AccessibleAlertCard
import cl.duoc.senalsegura.ui.components.AppTopBar
import cl.duoc.senalsegura.ui.components.PaletteHeader
import cl.duoc.senalsegura.ui.components.rememberAlertPaletteColors
import cl.duoc.senalsegura.viewmodel.AlertsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertsScreen(
    viewModel: AlertsViewModel,
    onBack: () -> Unit,
    onOpenAlert: (Int) -> Unit,
    onUseAlert: (AccessibleAlert) -> Unit
) {
    var categoryMenuOpen by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    val uiState = viewModel.uiState
    val visibleAlerts = viewModel.visibleAlerts
    val selectedCategoryLabel = uiState.selectedCategory?.label ?: "Todas"
    val palette = rememberAlertPaletteColors(
        drawableResource = R.drawable.alert_palette_art,
        fallbackDominant = MaterialTheme.colorScheme.primaryContainer,
        fallbackVibrant = MaterialTheme.colorScheme.primary,
        fallbackMuted = MaterialTheme.colorScheme.secondaryContainer
    )

    LaunchedEffect(Unit) {
        viewModel.loadFromContentProvider(context)
    }

    Scaffold(
        topBar = { AppTopBar(title = "Alertas accesibles", onBack = onBack) }
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 230.dp),
            modifier = Modifier.padding(innerPadding),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                PaletteHeader(
                    palette = palette,
                    providerMessage = uiState.providerMessage
                )
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                ExposedDropdownMenuBox(
                    expanded = categoryMenuOpen,
                    onExpandedChange = { categoryMenuOpen = !categoryMenuOpen }
                ) {
                    OutlinedTextField(
                        value = selectedCategoryLabel,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Categoría") },
                        supportingText = {
                            Text(AlertRepository.categoryDescription(uiState.selectedCategory))
                        },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = categoryMenuOpen
                            )
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = categoryMenuOpen,
                        onDismissRequest = { categoryMenuOpen = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Todas") },
                            onClick = {
                                viewModel.selectCategory(null)
                                categoryMenuOpen = false
                            }
                        )
                        AlertRepository.categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category.label) },
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
                    label = { Text("Buscar alerta") },
                    supportingText = {
                        Text("${visibleAlerts.size} de ${AlertRepository.alerts.size} alertas")
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Mostrar solo favoritas", fontWeight = FontWeight.Bold)
                        Text("Favoritas seleccionadas: ${uiState.favoriteIds.size}")
                    }
                    Switch(
                        checked = uiState.favoritesOnly,
                        onCheckedChange = viewModel::setFavoritesOnly
                    )
                }
            }

            if (visibleAlerts.isEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "No hay alertas que coincidan con los filtros.",
                            modifier = Modifier.padding(18.dp)
                        )
                    }
                }
            }

            items(visibleAlerts, key = { it.id }) { alert ->
                AccessibleAlertCard(
                    alert = alert,
                    isFavorite = viewModel.isFavorite(alert.id),
                    useCount = viewModel.useCount(alert.id),
                    palette = palette,
                    onOpen = { onOpenAlert(alert.id) },
                    onFavoriteChange = { viewModel.toggleFavorite(alert.id) },
                    onUse = { onUseAlert(alert) }
                )
            }
        }
    }
}
