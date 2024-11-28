@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authUseCase: AuthUseCase
) : ViewModel() {

    private val _authState = MutableLiveData<AuthResult<FirebaseUser?>>()
    val authState: LiveData<AuthResult<FirebaseUser?>> = _authState

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    fun signInAnonymously() {
        _loading.value = true
        viewModelScope.launch {
            val result = authUseCase.signInAnonymously()
            _authState.value = result
            _loading.value = false
        }
    }

    fun signInWithEmailAndPassword(email: String, password: String) {
        _loading.value = true
        viewModelScope.launch {
            val result = authUseCase.signInWithEmailAndPassword(email, password)
            _authState.value = result
            _loading.value = false
        }
    }

    fun signOut() {
        _loading.value = true
        viewModelScope.launch {
            val result = authUseCase.signOut()
            if (result is AuthResult.Success) {
                _authState.value = AuthResult.Idle
            }
            _loading.value = false
        }
    }
}
