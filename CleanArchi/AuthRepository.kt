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

    // NOUVEAU CREATE EN LIANT L'ANONYME AU NOUVEAU UTILISATEUR 
    fun createFirebaseUser(userRequest: AuthUserRequest): Flow<AuthResult<FirebaseUser>> = flow {
        try {
            val currentUser = firebaseAuth.currentUser
            if (currentUser != null && currentUser.isAnonymous) {
                // Lier l'utilisateur anonyme à l'email et au mot de passe
                val credential = EmailAuthProvider.getCredential(userRequest.email, userRequest.password)
                val authResult = currentUser.linkWithCredential(credential).await()

                // Récupérer l'utilisateur lié
                val firebaseUser = authResult.user ?: throw Exception("Erreur lors de la liaison de l'utilisateur.")

                // Mettre à jour le profil utilisateur si nécessaire
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(userRequest.displayName)
                    .build()
                firebaseUser.updateProfile(profileUpdates).await()

                // Envoyer un mail de vérification
                firebaseUser.sendEmailVerification().await()

                // Emettre un succès avec l'utilisateur lié
                emit(AuthResult.Success(firebaseUser))
            } else {
                AuthResult.Failure(Exception("Utilisateur non trouvé"))
            }
        } catch (e: FirebaseAuthUserCollisionException) {
            // Gérer le cas où l'utilisateur existe déjà
            emit(AuthResult.EmailExist)
        } catch (e: Exception) {
            // En cas d'erreur, émettre un échec
            emit(AuthResult.Failure(e))
        }
    }
}
