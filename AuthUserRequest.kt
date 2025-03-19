data class AuthUserRequest (
    var uid: String? = null,
    val email: String,
    val password: String,
    val displayName: String,
    val difficulty: String,
    val ageGroup: String,
    val device: String,
)
