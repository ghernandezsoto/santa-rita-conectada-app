package cl.santaritaconectada.app.network.response

// Se añade esta clase para representar un rol individual
data class Role(
    val id: Int,
    val name: String
)

// Se añade la lista de roles al modelo de Usuario
data class User(
    val id: Int,
    val name: String,
    val email: String,
    val roles: List<Role> // <-- ESTA ES LA LÍNEA CLAVE
)