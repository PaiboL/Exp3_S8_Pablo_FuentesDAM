package cl.duoc.senalsegura.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cl.duoc.senalsegura.ui.components.AppTopBar
import cl.duoc.senalsegura.util.UserValidator
import cl.duoc.senalsegura.viewmodel.AuthViewModel

@Composable
fun RecoveryScreen(viewModel: AuthViewModel, onBack: () -> Unit) {
    var email by rememberSaveable { mutableStateOf("") }
    var validationMessage by rememberSaveable { mutableStateOf("") }
    val uiState = viewModel.uiState

    LaunchedEffect(Unit) {
        viewModel.clearMessage()
    }

    Scaffold(
        topBar = { AppTopBar(title = "Recuperar contraseña", onBack = onBack) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp)
        ) {
            Text("🔐", style = MaterialTheme.typography.headlineMedium)
            Text(
                text = "Recupera tu acceso",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                if (viewModel.isFirebaseConfigured) {
                    "Ingresa el correo registrado. Firebase enviará las instrucciones de recuperación."
                } else {
                    "Ingresa el correo registrado. El respaldo local solo comprobará si la cuenta existe."
                }
            )
            Spacer(Modifier.height(20.dp))

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    validationMessage = ""
                    viewModel.clearMessage()
                },
                label = { Text("Correo electrónico") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = {
                    if (!UserValidator.isValidEmail(email)) {
                        validationMessage = "Ingresa un correo electrónico válido."
                    } else {
                        validationMessage = ""
                        viewModel.sendPasswordReset(email)
                    }
                },
                enabled = email.isNotBlank() && !uiState.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(if (uiState.isLoading) "Procesando…" else "Solicitar recuperación")
            }

            val resultMessage = validationMessage.ifBlank { uiState.message }
            val isSuccess = validationMessage.isBlank() && !uiState.isError
            if (resultMessage.isNotBlank()) {
                Spacer(Modifier.height(20.dp))
                Card(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = if (isSuccess) "✓ $resultMessage" else "⚠ $resultMessage",
                        color = if (isSuccess) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.error
                        },
                        modifier = Modifier.padding(18.dp)
                    )
                }
            }
        }
    }
}
