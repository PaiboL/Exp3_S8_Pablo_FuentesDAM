package cl.duoc.senalsegura.data

import androidx.compose.runtime.mutableStateListOf
import cl.duoc.senalsegura.model.User

object UserRepository {

    private val initialUsers = arrayOf(
        User("Pablo Fuentes", "pablo@duoc.cl", "1234", "Persona usuaria"),
        User("Ana Torres", "ana@senalsegura.cl", "1234", "Persona usuaria"),
        User("Carlos Pérez", "carlos@senalsegura.cl", "1234", "Acompañante"),
        User("María Soto", "maria@senalsegura.cl", "1234", "Persona usuaria"),
        User("Luis Rojas", "luis@senalsegura.cl", "1234", "Acompañante")
    )

    private val registeredUsers = mutableStateListOf(*initialUsers)

    val users: List<User>
        get() = registeredUsers.toList()

    fun authenticate(email: String, password: String): User? {
        return registeredUsers.firstOrNull {
            it.email.equals(email.trim(), ignoreCase = true) && it.password == password
        }
    }

    fun register(user: User): Boolean {
        val emailExists = registeredUsers.any {
            it.email.equals(user.email.trim(), ignoreCase = true)
        }
        if (emailExists) return false

        registeredUsers.add(user.copy(email = user.email.trim().lowercase()))
        return true
    }

    fun containsEmail(email: String): Boolean {
        return registeredUsers.any { it.email.equals(email.trim(), ignoreCase = true) }
    }
}

