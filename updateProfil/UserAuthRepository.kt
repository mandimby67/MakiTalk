package mg.gasydev.tenymalagasy.data.repository

import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import mg.gasydev.tenymalagasy.data.model.api.request.AuthUserRequest
import mg.gasydev.tenymalagasy.data.model.api.response.AuthUser
import mg.gasydev.tenymalagasy.data.service.ApiService
import javax.inject.Inject
import retrofit2.Response
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import mg.gasydev.tenymalagasy.domain.result.AuthResult
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class UserAuthRepository @Inject constructor(
    private val context: Context,
    private val api: ApiService,
    private val firebaseAuth: FirebaseAuth
) {

    // -----------------------------------------------------------CREATION---------------------------------------------------------
    fun createUser(userRequest: AuthUserRequest): Flow<AuthResult<AuthUser>> = flow {
        try {
            //userRequest.uid?.let { Log.i("DEBUG_CREATE", "uid==>" + it) }
            //Log.i("DEBUG_CREATE", "email==>" + userRequest.email)
            //Log.i("DEBUG_CREATE", "pass==>" + userRequest.password)
            //Log.i("DEBUG_CREATE", "name==>" + userRequest.displayName)
            //Log.i("DEBUG_CREATE", "age==>" + userRequest.ageGroup)
            //Log.i("DEBUG_CREATE", "difficulty==>" + userRequest.difficulty)
            //Log.i("DEBUG_CREATE", "device==>" + userRequest.device)
            val response: Response<AuthUser> = api.createUser(userRequest)
            if (response.isSuccessful) {
                response.body()?.let {
                    //Log.i("DEBUG_CREATE", "==>SUCCESS")
                    emit(AuthResult.Success(it))
                } ?: run {
                    //Log.i("DEBUG_CREATE", "==>FAILURE")
                    emit(AuthResult.Failure(Exception("Réponse vide")))
                }
            } else {
                //Log.i("DEBUG_CREATE", "==>IS NOT SUCCESS ==> code " + response.code() + "--message ==> " + response.message())
                emit(AuthResult.Failure(Exception("Erreur réseau : ${response.code()} ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(AuthResult.Failure(e))
        }
    }

    // ANCIEN CREATE ==> Besoin de changer l'UID de l'user anonyme dans la ROOM ==> NOUVEL UID de l'user créé (GACHI ANONYME DANS FIREBASE AUTH LIST)
    /*fun createFirebaseUser(userRequest: AuthUserRequest): Flow<AuthResult<FirebaseUser>> = flow {
        try {
            // 1. Créer un utilisateur avec l'email et le mot de passe
            val authResult = firebaseAuth.createUserWithEmailAndPassword(
                userRequest.email,
                userRequest.password
            ).await()

            // 2. Récupérer l'utilisateur créé
            val firebaseUser = authResult.user ?: throw Exception("Erreur lors de la création de l'utilisateur.")

            // 3. Mettre à jour le nom d'affichage si nécessaire
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(userRequest.displayName)
                .build()
            firebaseUser.updateProfile(profileUpdates).await()

            // 4. Envoyer un mail de verification
            firebaseUser.sendEmailVerification().await()

            // 5. Emettre un succès à ce stade, l'utilisateur a bien été créé
            emit(AuthResult.Success(firebaseUser))

            // 6. Se déconnecter après la création de l'utilisateur
            firebaseAuth.signOut()

            // 7. Reconnexion pour forcer la génération d'un token valide
            val signInResult = firebaseAuth
                .signInWithEmailAndPassword(userRequest.email, userRequest.password)
                .await()

            val refreshedUser = signInResult.user ?: throw Exception("Erreur lors de la reconnexion.")

            // 8. Forcer le rafraîchissement du token
            refreshedUser.getIdToken(true).await()

            // 9. Emettre un succès avec l'utilisateur reconnecté et token rafraîchi
            emit(AuthResult.Success(refreshedUser))
        } catch (e: FirebaseAuthUserCollisionException) {
            // Gérer le cas où l'utilisateur existe déjà
            emit(AuthResult.EmailExist)
        } catch (e: Exception) {
            // En cas d'erreur, émettre un échec
            emit(AuthResult.Failure(e))
        }
    }*/

    // NOUVEAU CREATE EN LIANT L'ANONYME AU NOUVEAU UTILISATEUR (PAS DE GACHI ANONYME DANS FIREBASE AUTH LIST)
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

    // --------------------------------------------------INFORMATION-----------------------------------------------
    fun getUser(uid: String): Flow<AuthResult<AuthUser>> = flow {
        try {
            //Log.i("DEBUG_GET_USER", "UID =>" + uid)
            val response: Response<AuthUser> = api.getUserById(uid)
            if (response.isSuccessful) {
                val userData = response.body()
                if (userData != null) {
                    emit(AuthResult.Success(userData))
                } else {
                    emit(AuthResult.Failure(Throwable("Données utilisateur manquantes")))  // Erreur si données manquantes
                }
            } else {
                emit(AuthResult.Failure(Exception("Erreur réseau : ${response.code()} ${response.message()}")))  // Erreur si échec réseau
            }
        } catch (e: Exception) {
            //Log.i("DEBUG_GET_USER", "Exception =>" + e.message)
            emit(AuthResult.Failure(e))  // Erreur lors de la requête réseau
        }
    }

    suspend fun isUserEmailVerified(): Boolean {
        val user = firebaseAuth.currentUser
        user?.reload()?.await() // Rafraîchit l'état de l'utilisateur
        //Log.i("checkUserEmailState", "user ==> ${user?.isEmailVerified}")
        return user?.isEmailVerified ?: false
    }

    suspend fun sendEmailVerification() {
        val user = firebaseAuth.currentUser
        user?.let {
            if (!it.isEmailVerified) {
                it.sendEmailVerification().await()
            }
        }
    }

    suspend fun sendEmailPasswordReset(email: String) {
        firebaseAuth.sendPasswordResetEmail(email).await()
    }

    //------------------------------------------SUPPRESSION---------------------------------------------------
    // Supprimer un utilisateur via l'API REST
    fun deleteUser(uid: String): Flow<AuthResult<Unit>> = flow {
        try {
            val response = api.deleteUser(uid)
            if (response.isSuccessful) {
                emit(AuthResult.Success(Unit))
            } else {
                emit(AuthResult.Failure(Exception("Erreur API REST : ${response.code()}")))
            }
        } catch (e: Exception) {
            emit(AuthResult.Failure(e))
        }
    }

    // Réauthentifier l'utilisateur
    fun reauthenticateUser(reauthEmail: String, reauthPassword: String): Flow<AuthResult<Unit>> = flow {
        try {
            val user = FirebaseAuth.getInstance().currentUser
                ?: throw Exception("Utilisateur non connecté")

            val credential = EmailAuthProvider.getCredential(reauthEmail, reauthPassword)

            suspendCancellableCoroutine<Unit> { continuation ->
                user.reauthenticate(credential).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        continuation.resume(Unit)
                    } else {
                        continuation.resumeWithException(
                            task.exception ?: Exception("Échec de la réauthentification")
                        )
                    }
                }
            }

            emit(AuthResult.Success(Unit))
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            emit(AuthResult.InvalidCredentials)
        } catch (e: FirebaseNetworkException) {
            emit(AuthResult.NetworkException)
        } catch (e: Exception) {
            emit(AuthResult.Failure(e))
        }
    }

    // Supprimer l'utilisateur de FirebaseAuth
    fun deleteFirebaseUser(): Flow<AuthResult<Unit>> = flow {
        try {
            val user = FirebaseAuth.getInstance().currentUser
                ?: throw Exception("Utilisateur non connecté")

            suspendCancellableCoroutine<Unit> { continuation ->
                user.delete().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        continuation.resume(Unit)
                    } else {
                        continuation.resumeWithException(
                            task.exception ?: Exception("Échec de la suppression Firebase")
                        )
                    }
                }
            }

            emit(AuthResult.Success(Unit))
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            emit(AuthResult.InvalidCredentials)
        } catch (e: FirebaseNetworkException) {
            emit(AuthResult.NetworkException)
        } catch (e: Exception) {
            emit(AuthResult.Failure(e))
        }
    }

}
