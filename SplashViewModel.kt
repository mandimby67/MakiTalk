package mg.gasydev.tenymalagasy.presentation.viewmodel.splash

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import mg.gasydev.tenymalagasy.data.service.firebase.EmailVerificationObserver
import mg.gasydev.tenymalagasy.domain.enumType.EmailSendStatus
import mg.gasydev.tenymalagasy.domain.enumType.UserAgeRange
import mg.gasydev.tenymalagasy.domain.enumType.UserDifficulty
import mg.gasydev.tenymalagasy.domain.enumType.UserType
import mg.gasydev.tenymalagasy.domain.usecase.AuthUseCase
import mg.gasydev.tenymalagasy.domain.usecase.CreateUserUseCase
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val emailVerificationObserver: EmailVerificationObserver,
    private val authUseCase: AuthUseCase,
    private val createUserUseCase: CreateUserUseCase,
) : ViewModel() {

    fun isLoggedIn(): Boolean {
        return authUseCase.isLoggedIn()
    }

    fun getAgeRange(): UserAgeRange {
        return authUseCase.getAgeRange()
    }

    fun getDifficulty(): UserDifficulty {
        return authUseCase.getDifficulty()
    }

    // Gestion email non verifié
    private val _isEmailVerified = MutableStateFlow(true)
    val isEmailVerified: StateFlow<Boolean> = _isEmailVerified

    fun observeEmailVerification() {
        emailVerificationObserver.observeEmailVerificationStatus()
            .onEach { isVerified ->
                _isEmailVerified.value = isVerified
            }
            .launchIn(viewModelScope)
    }

    private val _isEmailCheckable = MutableStateFlow(false)
    val isEmailCheckable: StateFlow<Boolean> = _isEmailCheckable

    fun checkUserEmailState() {
        viewModelScope.launch {
            try {
                val userType = authUseCase.getUserType()
                _isEmailCheckable.value = userType == UserType.AUNTHENTIFIED
                Log.i("checkUserEmailState", "==>" + userType.toString())
            } catch (e: Exception) {
                Log.e("checkUserEmailState", "Error in checkUserEmailState: ${e.message}")
            }
        }
    }

    private val _isUserEmailVerified = MutableStateFlow<Boolean?>(null)
    val isUserEmailVerified: StateFlow<Boolean?> = _isUserEmailVerified.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun checkIfUserEmailVerified() {
        Log.i("checkUserEmailState", "CALLED")
        viewModelScope.launch {
            _isLoading.value = true
            _isUserEmailVerified.value = createUserUseCase.isUserEmailVerified()
            _isLoading.value = false
        }
    }

    fun resetUserEmailVerified() {
        _isUserEmailVerified.value = null
    }

    // Gestion renvoi de mail pour verification
    private val _emailVerificationStatus = MutableStateFlow(EmailSendStatus.IDLE)
    val emailVerificationStatus: StateFlow<EmailSendStatus> = _emailVerificationStatus.asStateFlow()

    fun sendEmailVerification() {
        viewModelScope.launch {
            _emailVerificationStatus.value = EmailSendStatus.LOADING
            try {
                createUserUseCase.sendEmailVerification()
                _emailVerificationStatus.value = EmailSendStatus.SENT
            } catch (e: Exception) {
                _emailVerificationStatus.value = EmailSendStatus.ERROR
            }
        }
    }

}