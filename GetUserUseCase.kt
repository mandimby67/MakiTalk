package mg.gasydev.tenymalagasy.domain.usecase

import kotlinx.coroutines.flow.Flow
import mg.gasydev.tenymalagasy.data.model.api.response.AuthUser
import mg.gasydev.tenymalagasy.data.repository.UserAuthRepository
import mg.gasydev.tenymalagasy.domain.result.AuthResult
import javax.inject.Inject

class GetUserUseCase @Inject constructor(private val userAuthRepository: UserAuthRepository) {

    operator fun invoke(uid: String): Flow<AuthResult<AuthUser>> {
        return userAuthRepository.getUser(uid)
    }
}
