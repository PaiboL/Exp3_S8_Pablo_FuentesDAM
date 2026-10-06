package cl.duoc.senalsegura.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import cl.duoc.senalsegura.data.AccessibilityConfig
import cl.duoc.senalsegura.model.User
import cl.duoc.senalsegura.ui.components.AppTopBar
import cl.duoc.senalsegura.util.UserValidator
import cl.duoc.senalsegura.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    onBack: () -> Unit,
    onRegistrationSuccess: () -> Unit
) {
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var profile by rememberSaveable {
        mutableStateOf(AccessibilityConfig.profileTypes.first())
    }
    var preference by rememberSaveable {
        mutableStateOf(AccessibilityConfig.communicationPreferences.first())
    }
    var preferenceMenuOpen by rememberSaveable { mutableStateOf(false) }
    var termsAccepted by rememberSaveable { mutableStateOf(false) }
    var errorMessage by rememberSaveable { mutableStateOf("") }
    val uiState = viewModel.uiState

    LaunchedEffect(Unit) {
        viewModel.clearMessage()
    }
    Scaffold(
        topBar = { AppTopBar(title = "Registro de usuario", onBack = onBack) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            Text(
                text = "Crea tu perfil accesible",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                if (viewModel.isFirebaseConfigured) {
                    "La cuenta se crea con Firebase Authentication y su perfil se guarda en Realtime Database."
                } else {
                    "Sin configuración Firebase, la cuenta se crea temporalmente en el respaldo local de demostración."
                }
            )
            Spacer(Modifier.height(18.dp))

            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    errorMessage = ""
                    viewModel.clearMessage()
                },
                label = { Text("Nombre completo") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    errorMessage = ""
                    viewModel.clearMessage()
                },
                label = { Text("Correo electrónico") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    errorMessage = ""
                    viewModel.clearMessage()
                },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    errorMessage = ""
                    viewModel.clearMessage()
                },
                label = { Text("Confirmar contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(18.dp))
            Text("Tipo de perfil", style = MaterialTheme.typography.titleLarge)
            AccessibilityConfig.profileTypes.forEach { option ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = profile == option,
                        onClick = { profile = option }
                    )
                    Text(option)
                }
            }

            Spacer(Modifier.height(12.dp))
            ExposedDropdownMenuBox(
                expanded = preferenceMenuOpen,
                onExpandedChange = { preferenceMenuOpen = !preferenceMenuOpen }
            ) {
                OutlinedTextField(
                    value = preference,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Apoyo de comunicación preferido") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = preferenceMenuOpen)
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = preferenceMenuOpen,
                    onDismissRequest = { preferenceMenuOpen = false }
                ) {
                    AccessibilityConfig.communicationPreferences.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                preference = option
                                preferenceMenuOpen = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = termsAccepted,
                    onCheckedChange = { termsAccepted = it }
                )
                Text("Acepto el uso de mis datos para esta demostración académica.")
            }

            val visibleError = errorMessage.ifBlank {
                if (uiState.isError) uiState.message else ""
            }
            if (visibleError.isNotBlank()) {
                Text(
                    text = "⚠ $visibleError",
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(Modifier.height(18.dp))
            Button(
                onClick = {
                    val validationError = UserValidator.validateRegistration(
                        name = name,
                        email = email,
                        password = password,
                        confirmPassword = confirmPassword,
                        termsAccepted = termsAccepted
                    )

                    if (validationError != null) {
                        errorMessage = validationError
                    } else {
                        errorMessage = ""
                        viewModel.register(
                            User(name, email, password, profile, preference)
                        ) {
                            onRegistrationSuccess()
                        }
                    }
                },
                enabled = !uiState.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(if (uiState.isLoading) "Creando cuenta…" else "Crear cuenta")
            }
        }
    }
}
