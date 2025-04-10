package mg.gasydev.tenymalagasy.domain.usecase

import android.content.Context
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine
import mg.gasydev.tenymalagasy.R
import mg.gasydev.tenymalagasy.data.repository.ProfileAuthRepository
import mg.gasydev.tenymalagasy.domain.result.AuthResult
import javax.inject.Inject
import kotlin.coroutines.resume

class UpdateUserUseCase @Inject constructor(
    private val profileAuthRepository: ProfileAuthRepository,
    private val firebaseAuth: FirebaseAuth,
    @ApplicationContext private val context: Context
) {
    operator fun invoke(
        displayName: String? = null,
        photoUrl: String? = null,
        email: String? = null,
        reauthEmail: String? = null,
        reauthPassword: String? = null
    ): Flow<AuthResult<FirebaseUser>> = flow {
        val user = firebaseAuth.currentUser
        if (user == null) {
            emit(AuthResult.Failure(Exception("Aucun utilisateur connecté")))
            return@flow
        }

        // Si des informations de réauthentification sont fournies, effectuer la réauthentification
        if (reauthEmail != null && reauthPassword != null) {
            try {
                reauthenticateUser(user, reauthEmail, reauthPassword)
            } catch (e: FirebaseAuthInvalidCredentialsException) {
                emit(AuthResult.InvalidCredentials)
                return@flow
            } catch (e: FirebaseNetworkException) {
                emit(AuthResult.NetworkException)
                return@flow
            } catch (e: Exception) {
                emit(AuthResult.Failure(Exception(context.getString(R.string.auth_compte_connexion_erreur_serveur))))
                return@flow
            }
        }

        // Appeler le dépôt pour mettre à jour le profil
        emitAll(profileAuthRepository.updateUser(displayName, photoUrl, email))
    }

    private suspend fun reauthenticateUser(
        user: FirebaseUser,
        email: String,
        password: String
    ) {
        suspendCancellableCoroutine<Unit> { continuation ->
            val credential = EmailAuthProvider.getCredential(email, password)
            user.reauthenticate(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        continuation.resume(Unit)
                    } else {
                        val exception = task.exception ?: Exception("Erreur inconnue")
                        continuation.resumeWith(Result.failure(exception))
                    }
                }
        }
    }

    suspend fun sendEmailVerification(newEmail: String) {
        // Appel du repository pour envoyer l'email de vérification
        profileAuthRepository.sendEmailVerification(newEmail)
    }
}
