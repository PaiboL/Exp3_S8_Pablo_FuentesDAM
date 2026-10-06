package cl.duoc.senalsegura.data.firebase

import android.content.Context
import cl.duoc.senalsegura.data.UserRepository
import cl.duoc.senalsegura.model.User
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.UserProfileChangeRequest

class FirebaseAuthRepository(context: Context) {
    private val appContext = context.applicationContext

    val isFirebaseConfigured: Boolean
        get() = FirebaseBootstrap.isConfigured(appContext)

    val connectionLabel: String
        get() = FirebaseBootstrap.connectionLabel(appContext)

    fun login(
        email: String,
        password: String,
        onResult: (AuthenticationOutcome) -> Unit
    ) {
        val normalizedEmail = email.trim().lowercase()
        val auth = FirebaseBootstrap.authOrNull(appContext)

        if (auth == null) {
            val user = UserRepository.authenticate(normalizedEmail, password)
            onResult(
                if (user != null) {
                    AuthenticationOutcome(
                        session = AuthSession(user.name, user.email, usesFirebase = false),
                        message = "Sesión iniciada con el respaldo local"
                    )
                } else {
                    AuthenticationOutcome(
                        message = "Correo o contraseña incorrectos",
                        isError = true
                    )
                }
            )
            return
        }

        auth.signInWithEmailAndPassword(normalizedEmail, password)
            .addOnSuccessListener { result ->
                val firebaseUser = result.user
                val uid = firebaseUser?.uid.orEmpty()
                val fallbackName = firebaseUser?.displayName
                    ?: normalizedEmail.substringBefore("@").replaceFirstChar { character ->
                        character.uppercase()
                    }
                val profileReference = FirebaseBootstrap.databaseOrNull(appContext)
                    ?.getReference("users")
                    ?.child(uid)

                if (profileReference == null || uid.isBlank()) {
                    onResult(
                        AuthenticationOutcome(
                            session = AuthSession(fallbackName, normalizedEmail, true),
                            message = "Sesión autenticada con Firebase"
                        )
                    )
                } else {
                    profileReference.get()
                        .addOnSuccessListener { snapshot ->
                            val profile = snapshot.getValue(FirebaseUserProfile::class.java)
                            onResult(
                                AuthenticationOutcome(
                                    session = AuthSession(
                                        profile?.name?.ifBlank { fallbackName } ?: fallbackName,
                                        normalizedEmail,
                                        true
                                    ),
                                    message = "Sesión autenticada con Firebase"
                                )
                            )
                        }
                        .addOnFailureListener {
                            onResult(
                                AuthenticationOutcome(
                                    session = AuthSession(fallbackName, normalizedEmail, true),
                                    message = "Sesión autenticada; el perfil se sincronizará al conectarse"
                                )
                            )
                        }
                }
            }
            .addOnFailureListener { error ->
                onResult(AuthenticationOutcome(message = error.toReadableMessage(), isError = true))
            }
    }

    fun register(user: User, onResult: (AuthenticationOutcome) -> Unit) {
        val normalizedUser = user.copy(email = user.email.trim().lowercase())
        val auth = FirebaseBootstrap.authOrNull(appContext)

        if (auth == null) {
            val registered = UserRepository.register(normalizedUser)
            onResult(
                if (registered) {
                    AuthenticationOutcome(
                        message = "Cuenta creada temporalmente en el respaldo local. Ya puedes iniciar sesión."
                    )
                } else {
                    AuthenticationOutcome(
                        message = "El correo ya se encuentra registrado",
                        isError = true
                    )
                }
            )
            return
        }

        auth.createUserWithEmailAndPassword(normalizedUser.email, normalizedUser.password)
            .addOnSuccessListener { result ->
                val firebaseUser = result.user
                val uid = firebaseUser?.uid.orEmpty()
                if (firebaseUser == null || uid.isBlank()) {
                    onResult(
                        AuthenticationOutcome(
                            message = "Firebase no devolvió el usuario creado",
                            isError = true
                        )
                    )
                    return@addOnSuccessListener
                }

                firebaseUser.updateProfile(
                    UserProfileChangeRequest.Builder()
                        .setDisplayName(normalizedUser.name.trim())
                        .build()
                )

                val profile = FirebaseUserProfile(
                    uid = uid,
                    name = normalizedUser.name,
                    email = normalizedUser.email,
                    profile = normalizedUser.profile,
                    communicationPreference = normalizedUser.communicationPreference,
                    updatedAt = System.currentTimeMillis()
                ).normalized()

                val profileReference = FirebaseBootstrap.databaseOrNull(appContext)
                    ?.getReference("users")
                    ?.child(uid)

                if (profileReference == null) {
                    auth.signOut()
                    onResult(
                        AuthenticationOutcome(
                            message = "No fue posible acceder a Realtime Database",
                            isError = true
                        )
                    )
                    return@addOnSuccessListener
                }

                profileReference.setValue(profile)
                    .addOnSuccessListener {
                        auth.signOut()
                        onResult(AuthenticationOutcome(message = "Cuenta creada en Firebase"))
                    }
                    .addOnFailureListener { error ->
                        auth.signOut()
                        onResult(
                            AuthenticationOutcome(
                                message = "Cuenta autenticada, pero el perfil no pudo guardarse: ${error.toReadableMessage()}",
                                isError = true
                            )
                        )
                    }
            }
            .addOnFailureListener { error ->
                onResult(AuthenticationOutcome(message = error.toReadableMessage(), isError = true))
            }
    }

    fun sendPasswordReset(email: String, onResult: (AuthenticationOutcome) -> Unit) {
        val normalizedEmail = email.trim().lowercase()
        val auth = FirebaseBootstrap.authOrNull(appContext)

        if (auth == null) {
            val exists = UserRepository.containsEmail(normalizedEmail)
            onResult(
                if (exists) {
                    AuthenticationOutcome(
                        message = "La cuenta existe en el respaldo local; no se envió un correo real"
                    )
                } else {
                    AuthenticationOutcome(
                        message = "No encontramos una cuenta asociada a este correo",
                        isError = true
                    )
                }
            )
            return
        }

        auth.sendPasswordResetEmail(normalizedEmail)
            .addOnSuccessListener {
                onResult(AuthenticationOutcome(message = "Firebase envió el correo de recuperación"))
            }
            .addOnFailureListener { error ->
                onResult(AuthenticationOutcome(message = error.toReadableMessage(), isError = true))
            }
    }

    fun signOut() {
        FirebaseBootstrap.authOrNull(appContext)?.signOut()
    }

    private fun Throwable.toReadableMessage(): String = when (this) {
        is FirebaseAuthWeakPasswordException -> "La contraseña debe tener al menos 6 caracteres"
        is FirebaseAuthUserCollisionException -> "El correo ya se encuentra registrado"
        is FirebaseAuthInvalidCredentialsException -> "Las credenciales no son válidas"
        is FirebaseNetworkException -> "No hay conexión con Firebase. Revisa internet e inténtalo nuevamente"
        else -> localizedMessage ?: "Firebase no pudo completar la operación"
    }
}
