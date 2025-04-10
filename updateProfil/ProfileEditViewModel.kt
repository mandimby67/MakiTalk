package mg.gasydev.tenymalagasy.presentation.viewmodel.home.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mg.gasydev.tenymalagasy.data.model.api.response.AuthUserData
import mg.gasydev.tenymalagasy.data.repository.DatabaseRepository
import mg.gasydev.tenymalagasy.domain.enumType.EmailSendStatus
import mg.gasydev.tenymalagasy.domain.result.AuthResult
import mg.gasydev.tenymalagasy.domain.usecase.AuthUseCase
import mg.gasydev.tenymalagasy.domain.usecase.DeleteUserUseCase
import mg.gasydev.tenymalagasy.domain.usecase.ImageBucketUseCase
import mg.gasydev.tenymalagasy.domain.usecase.UpdateUserUseCase
import javax.inject.Inject

@HiltViewModel
class ProfileEditViewModel @Inject constructor (
    private val authUseCase: AuthUseCase,
    private val imageBucketUseCase: ImageBucketUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val deleteUserUseCase: DeleteUserUseCase,
    private val databaseRepository: DatabaseRepository
) : ViewModel() {

    private val _imageUrls = MutableStateFlow<List<String>>(emptyList())
    val imageUrls: StateFlow<List<String>> = _imageUrls

    private val _selectedImageUrl = MutableStateFlow<String?>(null)
    val selectedImageUrl: StateFlow<String?> = _selectedImageUrl

    private val _isLoadingImage = MutableStateFlow(false)
    val isLoadingImage: StateFlow<Boolean> = _isLoadingImage

    private val _isErrorMessageImage = MutableStateFlow(false)
    val isErrorMessageImage: StateFlow<Boolean> = _isErrorMessageImage


    // Champ name et email ==> Recuperer les donnees de preference-----------------------
    fun getUserDataFromPreferences(): AuthUserData {
        return AuthUserData(
            uid = authUseCase.getUserId(),
            displayName = authUseCase.getFullName(),
            email = authUseCase.getEmail(),
            photoURL = authUseCase.getPhotoUrl(),
            ageGroup = authUseCase.getAgeRange().valeur,
            difficulty = authUseCase.getDifficulty().valeur
        )
    }

    // Recuperation des images du bucket firebase--------------------------------
    fun fetchImageUrls() {
        viewModelScope.launch {
            _isLoadingImage.value = true
            _isErrorMessageImage.value = false
            try {
                val urls = imageBucketUseCase.execute()
                _imageUrls.value = urls
            } catch (e: Exception) {
                _isErrorMessageImage.value = true
            } finally {
                _isLoadingImage.value = false
            }
        }
    }

    // image selectionné sans confirmation pour la mise en surbrillance de la selection
    fun onImageSelected(url: String) {
        _selectedImageUrl.value = if (_selectedImageUrl.value == url) null else url
    }

    // Modification profile utilisateur-----------------------------------------------------
    private val _updateState = MutableStateFlow<AuthResult<FirebaseUser>>(AuthResult.Idle)
    val updateState: StateFlow<AuthResult<FirebaseUser>?> = _updateState

    private val _isLoadingUpdate = MutableStateFlow(false)
    val isLoadingUpdate: StateFlow<Boolean> = _isLoadingUpdate

    fun updateUser(
        displayName: String?,
        photoUrl: String?,
        email: String?,
        reauthEmail: String? = null,
        reauthPassword: String? = null
    ) {
        viewModelScope.launch {
            _isLoadingUpdate.value = true // Démarre le chargement
            try {
                updateUserUseCase(
                    displayName,
                    photoUrl,
                    email,
                    reauthEmail,
                    reauthPassword
                ).collect { result ->
                    if (result is AuthResult.Success) {
                        val userSuccess = result.data
                        authUseCase.saveUserDataToPreferences(userSuccess)
                    } else if (result is AuthResult.VerificationEmailSent) {
                        val userMailSent = result.data
                        authUseCase.saveUserDataToPreferences(userMailSent)
                    }
                    _updateState.value = result
                }
            } catch (e: Exception) {
                _updateState.value = AuthResult.Failure(e)
            } finally {
                _isLoadingUpdate.value = false // Arrête le chargement
            }
        }
    }

    // pour eviter d'appeler plusieurs fois le resultat de la requete
    fun resetUpdateState() {
        _updateState.value = AuthResult.Idle
    }

    // Renvoie de mail de confirmation vers le nouveau mail (emailsend deja affiché)
    private val _emailVerificationStatus = MutableStateFlow(EmailSendStatus.IDLE)
    val emailVerificationStatus: StateFlow<EmailSendStatus> = _emailVerificationStatus.asStateFlow()

    fun sendEmailVerification(newEmail: String) {
        viewModelScope.launch {
            _emailVerificationStatus.value = EmailSendStatus.LOADING
            try {
                updateUserUseCase.sendEmailVerification(newEmail)
                _emailVerificationStatus.value = EmailSendStatus.SENT
            } catch (e: Exception) {
                _emailVerificationStatus.value = EmailSendStatus.ERROR
            }
        }
    }

    // Effacer les données liées a l'utilisateur
    fun clearUserPreferences() {
        authUseCase.clearUserPreferences()
    }

    // Supression profile utilisateur-----------------------------------------------------
    // État pour le résultat de la suppression
    private val _deleteState = MutableStateFlow<AuthResult<Unit>>(AuthResult.Idle)
    val deleteState: StateFlow<AuthResult<Unit>> = _deleteState.asStateFlow()

    // Indicateur de chargement pour l'opération de suppression
    private val _isLoadingDelete = MutableStateFlow(false)
    val isLoadingDelete: StateFlow<Boolean> = _isLoadingDelete.asStateFlow()

    // Méthode pour supprimer un utilisateur
    fun deleteUser(uid: String, reauthEmail: String, reauthPassword: String) {
        Log.i("DEBUG_DELETE", "uid==>" + uid)
        Log.i("DEBUG_DELETE", "reauthEmail==>" + reauthEmail)
        Log.i("DEBUG_DELETE", "reauthPassword==>" + reauthPassword)
        viewModelScope.launch {
            _isLoadingDelete.value = true
            deleteUserUseCase.deleteUser(uid, reauthEmail, reauthPassword).collect { result ->
                _deleteState.value = result
            }
            // supprimer la base de données de l'app
            databaseRepository.clearDatabase()
            _isLoadingDelete.value = false
        }
    }

    // pour eviter d'appeler plusieurs fois le resultat de la requete
    fun resetDeleteState() {
        _deleteState.value = AuthResult.Idle
    }

}
