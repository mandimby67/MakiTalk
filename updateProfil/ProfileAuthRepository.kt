package mg.gasydev.tenymalagasy.data.repository

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import mg.gasydev.tenymalagasy.domain.result.AuthResult
import javax.inject.Inject

class ProfileAuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {

    fun updateUser(
        displayName: String? = null,
        photoUrl: String? = null,
        email: String? = null
    ): Flow<AuthResult<FirebaseUser>> = flow {
        val user = firebaseAuth.currentUser
        if (user != null) {
            try {
                // Mise à jour du displayName et de la photo de profil si spécifiés
                if (displayName != null || photoUrl != null) {
                    val profileUpdates = userProfileChangeRequest {
                        if (displayName != user.displayName) displayName?.let { this.displayName = it }
                        if (photoUrl != user.photoUrl?.toString()) photoUrl?.let { this.photoUri = Uri.parse(it) }
                    }
                    user.updateProfile(profileUpdates).await()
                }

                // Vérification avant de mettre à jour l'email
                email?.let { newEmail ->
                    if (newEmail != user.email) {  // Vérifie si l'email est différent de l'email actuel
                        try {
                            user.verifyBeforeUpdateEmail(newEmail).await()
                            emit(AuthResult.VerificationEmailSent(user))
                            return@flow
                        } catch (e: FirebaseAuthUserCollisionException) {
                            emit(AuthResult.EmailExist)
                            return@flow
                        }
                    }
                }

                // Recharge les informations de l'utilisateur pour obtenir les dernières mises à jour
                user.reload().await()
                emit(AuthResult.Success(user)) // Émettre l'utilisateur mis à jour
            } catch (e: Exception) {
                emit(AuthResult.Failure(e))
            }
        } else {
            emit(AuthResult.Failure(Exception("Utilisateur non connecté")))
        }
    }.catch { e ->
        emit(AuthResult.Failure(e))
    }

    suspend fun sendEmailVerification(newEmail: String) {
        val user = firebaseAuth.currentUser
        user?.let {
            try {
                // Envoi de l'email de vérification pour la nouvelle adresse email
                it.verifyBeforeUpdateEmail(newEmail).await()
            } catch (e: Exception) {
                throw e  // Propagation de l'erreur, à gérer plus haut
            }
        }
    }

}
