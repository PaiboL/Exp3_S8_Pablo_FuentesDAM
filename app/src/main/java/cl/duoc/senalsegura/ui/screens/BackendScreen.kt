package cl.duoc.senalsegura.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cl.duoc.senalsegura.data.AccessibilityConfig
import cl.duoc.senalsegura.data.firebase.FirebaseAlertRecord
import cl.duoc.senalsegura.ui.components.AppTopBar
import cl.duoc.senalsegura.viewmodel.BackendViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackendScreen(
    viewModel: BackendViewModel,
    userName: String,
    userEmail: String,
    onBack: () -> Unit
) {
    val uiState = viewModel.uiState
    val visibleAlerts = viewModel.visibleAlerts
    var categoryMenuOpen by rememberSaveable { mutableStateOf(false) }
    var preferenceMenuOpen by rememberSaveable { mutableStateOf(false) }
    var alertPendingDelete by rememberSaveable { mutableStateOf<String?>(null) }
    var profileDeleteDialogOpen by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(userEmail) {
        viewModel.start(userName, userEmail)
    }
    DisposableEffect(Unit) {
        onDispose { viewModel.stopObserving() }
    }

    alertPendingDelete?.let { recordId ->
        AlertDialog(
            onDismissRequest = { alertPendingDelete = null },
            title = { Text("Eliminar alerta") },
            text = { Text("Esta operación eliminará el registro de la base de datos.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteAlert(recordId)
                        alertPendingDelete = null
                    }
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { alertPendingDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (profileDeleteDialogOpen) {
        AlertDialog(
            onDismissRequest = { profileDeleteDialogOpen = false },
            title = { Text("Eliminar perfil") },
            text = {
                Text("Se eliminará el perfil de Realtime Database. La cuenta de Authentication se conserva.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteProfile()
                        profileDeleteDialogOpen = false
                    }
                ) {
                    Text("Eliminar perfil")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { profileDeleteDialogOpen = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        topBar = { AppTopBar(title = "Back end y Firebase", onBack = onBack) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = if (viewModel.isFirebaseConfigured) {
                                "Sincronización Firebase activa"
                            } else {
                                "Demostración local disponible"
                            },
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(viewModel.connectionLabel)
                        Text(
                            text = "CRUD implementado con setValue, updateChildren y removeValue.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            item {
                Text("Perfil de usuario", style = MaterialTheme.typography.headlineSmall)
            }

            item {
                OutlinedTextField(
                    value = uiState.profileName,
                    onValueChange = viewModel::updateProfileName,
                    label = { Text("Nombre") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                OutlinedTextField(
                    value = uiState.profile.email.ifBlank { userEmail },
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Correo autenticado") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                ExposedDropdownMenuBox(
                    expanded = preferenceMenuOpen,
                    onExpandedChange = { preferenceMenuOpen = !preferenceMenuOpen }
                ) {
                    OutlinedTextField(
                        value = uiState.profilePreference,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Preferencia de comunicación") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = preferenceMenuOpen
                            )
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = preferenceMenuOpen,
                        onDismissRequest = { preferenceMenuOpen = false }
                    ) {
                        AccessibilityConfig.communicationPreferences.forEach { preference ->
                            DropdownMenuItem(
                                text = { Text(preference) },
                                onClick = {
                                    viewModel.updateProfilePreference(preference)
                                    preferenceMenuOpen = false
                                }
                            )
                        }
                    }
                }
            }

            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = viewModel::saveProfile,
                        enabled = !uiState.isSaving,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Actualizar perfil")
                    }
                    OutlinedButton(
                        onClick = { profileDeleteDialogOpen = true },
                        enabled = !uiState.isSaving,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Eliminar perfil")
                    }
                }
            }

            item {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = if (uiState.editingId == null) {
                        "Crear alerta persistente"
                    } else {
                        "Editar alerta persistente"
                    },
                    style = MaterialTheme.typography.headlineSmall
                )
            }

            item {
                OutlinedTextField(
                    value = uiState.title,
                    onValueChange = viewModel::updateTitle,
                    label = { Text("Título") },
                    supportingText = { Text("${uiState.title.length}/60") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                OutlinedTextField(
                    value = uiState.message,
                    onValueChange = viewModel::updateMessage,
                    label = { Text("Mensaje accesible") },
                    supportingText = { Text("${uiState.message.length}/180") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                ExposedDropdownMenuBox(
                    expanded = categoryMenuOpen,
                    onExpandedChange = { categoryMenuOpen = !categoryMenuOpen }
                ) {
                    OutlinedTextField(
                        value = uiState.category,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Categoría") },
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
                        listOf("General", "Seguridad", "Salud").forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category) },
                                onClick = {
                                    viewModel.updateCategory(category)
                                    categoryMenuOpen = false
                                }
                            )
                        }
                    }
                }
            }

            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = viewModel::saveAlert,
                        enabled = !uiState.isSaving,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(if (uiState.editingId == null) "Crear" else "Guardar cambios")
                    }
                    if (uiState.editingId != null) {
                        OutlinedButton(
                            onClick = viewModel::cancelEdit,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancelar")
                        }
                    }
                }
            }

            if (uiState.statusMessage.isNotBlank()) {
                item {
                    Text(
                        text = if (uiState.isError) {
                            "⚠ ${uiState.statusMessage}"
                        } else {
                            "✓ ${uiState.statusMessage}"
                        },
                        color = if (uiState.isError) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.primary
                        },
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            item {
                Text(
                    text = "Alertas sincronizadas (${visibleAlerts.size} de ${uiState.alerts.size})",
                    style = MaterialTheme.typography.headlineSmall
                )
            }

            item {
                OutlinedTextField(
                    value = uiState.searchText,
                    onValueChange = viewModel::updateSearchText,
                    label = { Text("Buscar alerta sincronizada") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (visibleAlerts.isEmpty()) {
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = if (uiState.alerts.isEmpty()) {
                                "No hay alertas almacenadas."
                            } else {
                                "No hay alertas que coincidan con la búsqueda."
                            },
                            modifier = Modifier.padding(18.dp)
                        )
                    }
                }
            }

            items(visibleAlerts, key = { record -> record.id }) { record ->
                FirebaseAlertCard(
                    record = record,
                    onEdit = { viewModel.editAlert(record) },
                    onDelete = { alertPendingDelete = record.id }
                )
            }
        }
    }
}

@Composable
private fun FirebaseAlertCard(
    record: FirebaseAlertRecord,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(record.title, style = MaterialTheme.typography.titleLarge)
            Text(record.category, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(6.dp))
            Text(record.message)
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(onClick = onEdit, modifier = Modifier.weight(1f)) {
                    Text("Editar")
                }
                OutlinedButton(onClick = onDelete, modifier = Modifier.weight(1f)) {
                    Text("Eliminar")
                }
            }
        }
    }
}
