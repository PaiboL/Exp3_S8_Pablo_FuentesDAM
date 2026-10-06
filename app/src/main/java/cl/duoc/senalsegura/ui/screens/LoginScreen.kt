package cl.duoc.senalsegura.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.senalsegura.data.firebase.AuthSession
import cl.duoc.senalsegura.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onLoginSuccess: (AuthSession) -> Unit,
    onRegister: () -> Unit,
    onRecovery: () -> Unit
) {
    var email by rememberSaveable {
        mutableStateOf(if (viewModel.isFirebaseConfigured) "" else "pablo@duoc.cl")
    }
    var password by rememberSaveable {
        mutableStateOf(if (viewModel.isFirebaseConfigured) "" else "1234")
    }
    val haptic = LocalHapticFeedback.current
    val uiState = viewModel.uiState

    LaunchedEffect(Unit) {
        viewModel.clearMessage()
    }
    LaunchedEffect(uiState.isError) {
        if (uiState.isError) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("💬", fontSize = 58.sp)
            Text(
                text = "SeñalSegura",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Comunicación clara, visible y accesible",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(Modifier.height(24.dp))

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Iniciar sesión",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(Modifier.height(16.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
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
                            viewModel.clearMessage()
                        },
                        label = { Text("Contraseña") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (uiState.message.isNotBlank()) {
                        Spacer(Modifier.height(10.dp))
                        Text(
                            text = if (uiState.isError) {
                                "⚠ ${uiState.message}"
                            } else {
                                "✓ ${uiState.message}"
                            },
                            color = if (uiState.isError) {
                                MaterialTheme.colorScheme.error
                            } else {
                                MaterialTheme.colorScheme.primary
                            },
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(Modifier.height(18.dp))

                    Button(
                        onClick = {
                            viewModel.login(email, password) { session ->
                                onLoginSuccess(session)
                            }
                        },
                        enabled = email.isNotBlank() &&
                            password.isNotBlank() &&
                            !uiState.isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    ) {
                        Text(
                            if (uiState.isLoading) "Conectando…" else "Ingresar",
                            fontSize = 18.sp
                        )
                    }
                }
            }

            TextButton(onClick = onRecovery) {
                Text("¿Olvidaste tu contraseña?")
            }
            TextButton(onClick = onRegister) {
                Text("Crear una cuenta nueva")
            }

            Spacer(Modifier.height(8.dp))
            Text(
                text = viewModel.connectionLabel,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            if (!viewModel.isFirebaseConfigured) {
                Text(
                    text = "Acceso local: pablo@duoc.cl / 1234",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
