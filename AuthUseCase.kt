package mg.gasydev.tenymalagasy.domain.usecase

import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import mg.gasydev.tenymalagasy.R
import mg.gasydev.tenymalagasy.data.model.api.response.AuthUser
import mg.gasydev.tenymalagasy.domain.enumType.UserAgeRange
import mg.gasydev.tenymalagasy.domain.enumType.UserDifficulty
import mg.gasydev.tenymalagasy.domain.enumType.UserType
import mg.gasydev.tenymalagasy.domain.preferences.UserPreferencesManager
import mg.gasydev.tenymalagasy.domain.result.AuthResult
import javax.inject.Inject

class AuthUseCase @Inject constructor(
    private val userPreferencesManager: UserPreferencesManager,
    private val firebaseAuth: FirebaseAuth,
    @ApplicationContext private val context: Context
) {

    // modification
    fun setUserType(userType: UserType) {
        userPreferencesManager.setUserType(userType)
    }

    fun setUserId(userId: String) {
        userPreferencesManager.setUserId(userId)
    }

    // recuperation
    fun getEmail(): String? {
        return userPreferencesManager.getEmail()
    }

    fun getFullName(): String? {
        return userPreferencesManager.getFullName()
    }

    fun getUserId(): String? {
        return userPreferencesManager.getUserId()
    }

    fun getUserType(): UserType {
        return userPreferencesManager.getUserType()
    }

    fun getAgeRange(): UserAgeRange {
        return userPreferencesManager.getAgeRange()
    }

    fun getDifficulty(): UserDifficulty {
        return userPreferencesManager.getDifficulty()
    }

    fun getPhotoUrl(): String? {
        return userPreferencesManager.getPhotoUrl()
    }

    fun isLoggedIn(): Boolean {
        return userPreferencesManager.isLoggedIn()
    }

    suspend fun signInAnonymously(): AuthResult<FirebaseUser?> {
        return try {
            val authResult = firebaseAuth.signInAnonymously().await()
            val user = authResult.user
            if (user != null) {
                Log.d("AuthUseCase", "Connexion anonyme réussie: ${user.uid}")
                setUserId(user.uid)
                userPreferencesManager.setUserType(UserType.ANONYMOUS)
                userPreferencesManager.setIsLoggedIn(true)
                AuthResult.Success(user)
            } else {
                AuthResult.Failure(Exception("Utilisateur non trouvé"))
            }
        } catch (e: Exception) {
            Log.e("AuthUseCase", "Erreur lors de la connexion anonyme: ${e.message}")
            AuthResult.Failure(e)
        }
    }

    suspend fun signInWithEmailAndPassword(email: String, password: String): AuthResult<FirebaseUser?> {
        return try {
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val user = authResult.user
            if (user != null) {
                if (user.isEmailVerified) {
                    Log.i("DEBUG_AUTH", "Success EmailVerified")
                    AuthResult.Success(user)
                } else {
                    Log.i("DEBUG_AUTH", "Success Email Not Verified")
                    AuthResult.Failure(Exception(context.getString(R.string.auth_compte_connexion_non_verifier)))
                }
            } else {
                AuthResult.Failure(Exception(context.getString(R.string.auth_compte_connexion_non_trouver)))
            }
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Log.i("DEBUG_AUTH", "FirebaseAuthInvalidCredentialsException")
            AuthResult.InvalidCredentials
        } catch (e: FirebaseNetworkException) {
            Log.i("DEBUG_AUTH", "FirebaseNetworkException")
            AuthResult.NetworkException
        } catch (e: Exception) {
            Log.i("DEBUG_AUTH", "Exception")
            AuthResult.Failure(Exception(context.getString(R.string.auth_compte_connexion_erreur_serveur)))
        }
        /*======> ereur ici <=======*/
    }

    // Pour le sauvegarde apres inscription et authentification (données personnalisées et non personnalisées)
    fun saveUserDataToPreferences(authUser: AuthUser) {
        authUser.data?.uid?.let { setUserId(it) }
        authUser.data?.displayName?.let { userPreferencesManager.setFullName(it) }
        authUser.data?.email?.let { userPreferencesManager.setEmail(it) }
        authUser.data?.photoURL?.let { userPreferencesManager.setPhotoUrl(it) }
        authUser.data?.difficulty?.let {
            userPreferencesManager.setDifficulty(UserDifficulty.fromString(it))
        }
        authUser.data?.ageGroup?.let {
            userPreferencesManager.setAgeRange(UserAgeRange.fromString(it))
        }

        userPreferencesManager.setIsLoggedIn(true)
    }

    // Seulement pour le sauvegarde apres la modification du profile (données non personnalisées)
    fun saveUserDataToPreferences(firebaseUser: FirebaseUser) {
        firebaseUser.displayName?.let { userPreferencesManager.setFullName(it) }
        firebaseUser.email?.let { userPreferencesManager.setEmail(it) }
        firebaseUser.photoUrl?.let { userPreferencesManager.setPhotoUrl(it.toString()) }
    }

    fun signOut() {
        try {
            firebaseAuth.signOut()
        } catch (e: Exception) {
            Log.e("AuthUseCase", "SIGNOUT FAILED")
        }
    }

    fun clearUserPreferences() {
        userPreferencesManager.clearUserPreferences()
    }
}