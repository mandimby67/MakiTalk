class AuthUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userPreferencesManager: UserPreferencesManager
) {

    suspend fun signInAnonymously(): AuthResult<FirebaseUser?> {
        val result = authRepository.signInAnonymously()
        if (result is AuthResult.Success) {
            val user = result.data
            user?.let {
                userPreferencesManager.setUserId(it.uid)
                userPreferencesManager.setUserType(UserType.ANONYMOUS)
                userPreferencesManager.setIsLoggedIn(true)
            }
        }
        return result
    }

    suspend fun signInWithEmailAndPassword(email: String, password: String): AuthResult<FirebaseUser?> {
        val result = authRepository.signInWithEmailAndPassword(email, password)
        if (result is AuthResult.Success) {
            val user = result.data
            user?.let {
                userPreferencesManager.setUserId(it.uid)
                userPreferencesManager.setUserType(UserType.REGISTERED)
                userPreferencesManager.setIsLoggedIn(true)
            }
        }
        return result
    }

    suspend fun signOut(): AuthResult<Unit> {
        val result = authRepository.signOut()
        if (result is AuthResult.Success) {
            userPreferencesManager.clearPreferences()
        }
        return result
    }
}
