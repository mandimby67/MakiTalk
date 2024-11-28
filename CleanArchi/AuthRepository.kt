class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    @ApplicationContext private val context: Context
) {

    suspend fun signInAnonymously(): AuthResult<FirebaseUser?> {
        return try {
            val authResult = firebaseAuth.signInAnonymously().await()
            val user = authResult.user
            if (user != null) {
                Log.d("AuthRepository", "Connexion anonyme réussie: ${user.uid}")
                AuthResult.Success(user)
            } else {
                AuthResult.Failure(Exception("Utilisateur non trouvé"))
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Erreur lors de la connexion anonyme: ${e.message}")
            AuthResult.Failure(e)
        }
    }

    suspend fun signInWithEmailAndPassword(email: String, password: String): AuthResult<FirebaseUser?> {
        return try {
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val user = authResult.user
            if (user != null) {
                if (user.isEmailVerified) {
                    Log.i("AuthRepository", "Connexion réussie: Email vérifié")
                    AuthResult.Success(user)
                } else {
                    Log.i("AuthRepository", "Connexion réussie: Email non vérifié")
                    AuthResult.Failure(Exception(context.getString(R.string.auth_compte_connexion_non_verifier)))
                }
            } else {
                AuthResult.Failure(Exception(context.getString(R.string.auth_compte_connexion_non_trouver)))
            }
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Log.i("AuthRepository", "FirebaseAuthInvalidCredentialsException")
            AuthResult.InvalidCredentials
        } catch (e: FirebaseNetworkException) {
            Log.i("AuthRepository", "FirebaseNetworkException")
            AuthResult.NetworkException
        } catch (e: Exception) {
            Log.i("AuthRepository", "Exception")
            AuthResult.Failure(Exception(context.getString(R.string.auth_compte_connexion_erreur_serveur)))
        }
    }

    suspend fun signOut(): AuthResult<Unit> {
        return try {
            firebaseAuth.signOut()
            Log.d("AuthRepository", "Déconnexion réussie")
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Erreur lors de la déconnexion: ${e.message}")
            AuthResult.Failure(e)
        }
    }
}
