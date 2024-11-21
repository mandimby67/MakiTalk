package mg.gasydev.tenymalagasy.utils

object ValidationUtils {

    fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isValidPassword(password: String): Boolean {
        // Vérifier la longueur minimale (au moins 8 caractères)
        if (password.length < 8) {
            return false
        }

        // Vérifier si le mot de passe contient au moins une lettre majuscule
        val hasUpperCase = password.any { it.isUpperCase() }

        // Vérifier si le mot de passe contient au moins une lettre minuscule
        val hasLowerCase = password.any { it.isLowerCase() }

        // Vérifier si le mot de passe contient une lettre
        val hasLetter = password.any { it.isLetter() }

        // Vérifier si le mot de passe contient au moins un chiffre
        val hasDigit = password.any { it.isDigit() }

        // Vérifier si le mot de passe contient au moins un caractère spécial
        val specialCharacters = "!@#\$%^&*()-_+=<>?/{}[]|\\:;\"'.,"
        val hasSpecialChar = password.any { it in specialCharacters }

        // Retourner true si toutes les conditions sont remplies
        //return hasUpperCase && hasLowerCase && hasDigit && hasSpecialChar
        return hasLetter && hasDigit
    }

}