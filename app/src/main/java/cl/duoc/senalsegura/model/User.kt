package cl.duoc.senalsegura.model

data class User(
    val name: String,
    val email: String,
    val password: String,
    val profile: String = "Persona usuaria",
    val communicationPreference: String = "Texto grande"
)

