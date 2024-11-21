package mg.gasydev.tenymalagasy.presentation.viewmodel.auth

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import mg.gasydev.tenymalagasy.data.model.api.response.AuthUser
import mg.gasydev.tenymalagasy.domain.enumType.UserType
import mg.gasydev.tenymalagasy.domain.result.AuthResult
import mg.gasydev.tenymalagasy.domain.usecase.AuthUseCase
import mg.gasydev.tenymalagasy.domain.usecase.GetUserUseCase
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase,
    private val authUseCase: AuthUseCase,
    private val savedStateHandle: SavedStateHandle,
): ViewModel() {

    fun getUserType(): UserType {
        return authUseCase.getUserType()
    }

    fun setUserType(userType: UserType) {
        authUseCase.setUserType(userType)
    }

    // authentification anonyme pour obtenir un token anonyme => bouton "COMMENCER"
    fun signInAnonymously(onResult: (AuthResult<FirebaseUser?>) -> Unit) {
        viewModelScope.launch {
            val result = authUseCase.signInAnonymously()
            onResult(result)
        }
    }

    // authentification classique => bouton "SE CONNECTER"
    fun signInWithEmailAndPassword(
        email: String,
        password: String,
        onResult: (AuthResult<AuthUser?>) -> Unit
    ) {
        viewModelScope.launch {
            val result = authUseCase.signInWithEmailAndPassword(email, password)
            when (result) {
                is AuthResult.Success -> {
                    result.data?.uid?.let { uid ->
                        getUserUseCase(uid).collect { resultUser ->
                            if (resultUser is AuthResult.Success) {
                                authUseCase.saveUserDataToPreferences(resultUser.data)
                            }
                            onResult(resultUser)
                        }
                    }
                }
                is AuthResult.Failure -> {
                    onResult(result)
                }
                AuthResult.InvalidCredentials -> {
                    onResult(AuthResult.InvalidCredentials)
                }
                AuthResult.NetworkException -> {
                    onResult(AuthResult.NetworkException)
                }
                AuthResult.Unauthorized -> {
                    onResult(AuthResult.Unauthorized)
                }
                AuthResult.EmailExist -> {
                    onResult(AuthResult.EmailExist)
                }
                else -> {}
            }
        }
    }

    // Dernier card ouvert
    companion object {
        private const val AUTH_EXPANDED_CARD_KEY = "auth_expanded_card_key"
    }

    // Récupération de l'état initial de la carte ouverte
    private val initialOpened: Int
        get() {
            // Si `expandedCard` a été enregistré dans `SavedStateHandle`, on l'utilise
            return savedStateHandle[AUTH_EXPANDED_CARD_KEY] ?: run {
                // Sinon, on utilise `getUserType()` pour définir la valeur initiale
                if (getUserType() == UserType.AUNTHENTIFIED) 0 else 1
            }
        }

    // Gestion de l'état `expandedCard` dans `SavedStateHandle`
    var expandedCard: Int
        get() = savedStateHandle[AUTH_EXPANDED_CARD_KEY] ?: initialOpened
        set(value) {
            savedStateHandle[AUTH_EXPANDED_CARD_KEY] = value
        }

    fun onCardSelected(cardIndex: Int) {
        expandedCard = cardIndex
    }

}