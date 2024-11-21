package mg.gasydev.tenymalagasy.domain.result

sealed class AuthResult<out T> {
    data class Success<out T>(val data: T) : AuthResult<T>()
    data class Failure(val error: Throwable) : AuthResult<Nothing>()
    object EmailExist : AuthResult<Nothing>()
    object Unauthorized : AuthResult<Nothing>()
    object NetworkException : AuthResult<Nothing>()
    object InvalidCredentials : AuthResult<Nothing>()
    data class VerificationEmailSent<out T>(val data: T) : AuthResult<T>()
    object Idle : AuthResult<Nothing>()
}