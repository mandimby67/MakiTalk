package mg.gasydev.tenymalagasy.domain.usecase

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import mg.gasydev.tenymalagasy.data.repository.UserAuthRepository
import mg.gasydev.tenymalagasy.domain.result.AuthResult
import javax.inject.Inject
class DeleteUserUseCase @Inject constructor(private val userAuthRepository: UserAuthRepository) {

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun deleteUser(
        uid: String,
        reauthEmail: String,
        reauthPassword: String
    ): Flow<AuthResult<Unit>> {
        return userAuthRepository.reauthenticateUser(reauthEmail, reauthPassword).flatMapConcat { reauthResult: AuthResult<Unit> ->
            when (reauthResult) {
                is AuthResult.Success -> {
                    userAuthRepository.deleteUser(uid).flatMapConcat { apiResult: AuthResult<Unit> ->
                        when (apiResult) {
                            is AuthResult.Success -> userAuthRepository.deleteFirebaseUser()
                            is AuthResult.Failure -> flow { emit(apiResult) }
                            else -> flow { emit(AuthResult.Failure(Exception("Erreur inconnue"))) }
                        }
                    }
                }
                else -> flow { emit(reauthResult) }
            }
        }
    }

}
