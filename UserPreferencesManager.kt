package mg.gasydev.tenymalagasy.domain.preferences

import android.content.Context
import android.content.SharedPreferences
import mg.gasydev.tenymalagasy.domain.enumType.UserAgeRange
import mg.gasydev.tenymalagasy.domain.enumType.UserDifficulty
import mg.gasydev.tenymalagasy.domain.enumType.UserType

class UserPreferencesManager(context: Context) {

    companion object {
        private const val PREFERENCES_NAME = "user_preferences"
        private const val KEY_USER_ID = "key_user_id"
        private const val KEY_USER_TYPE = "key_user_type"
        private const val KEY_FULL_NAME = "key_full_name"
        private const val KEY_EMAIL = "key_email"
        private const val KEY_PHOTO = "key_photo"
        private const val KEY_AGE_RANGE = "key_age_range"
        private const val KEY_DIFFICULTY = "key_difficulty"

        // temoin d'authentification
        private const val KEY_IS_LOGGED_IN = "key_is_logged_in"
    }

    private val preferences: SharedPreferences =
        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    // Stocker l'userId------------------------------------------------------
    fun setUserId(userId: String) {
        preferences.edit().putString(KEY_USER_ID, userId).apply()
    }

    fun getUserId(): String? {
        return preferences.getString(KEY_USER_ID, null)
    }

    // Stocker l'userType------------------------------------------------------
    fun setUserType(userType: UserType) {
        preferences.edit().putString(KEY_USER_TYPE, userType.name).apply()
    }

    fun getUserType(): UserType {
        val value = preferences.getString(KEY_USER_TYPE, null)
        return if (value != null) UserType.valueOf(value) else UserType.IDLE
    }

    // Stocker le nom complet------------------------------------------------------
    fun setFullName(fullName: String) {
        preferences.edit().putString(KEY_FULL_NAME, fullName).apply()
    }

    fun getFullName(): String? {
        return preferences.getString(KEY_FULL_NAME, null)
    }

    // Stocker l'email------------------------------------------------------
    fun setEmail(email: String) {
        preferences.edit().putString(KEY_EMAIL, email).apply()
    }

    fun getEmail(): String? {
        return preferences.getString(KEY_EMAIL, null)
    }

    // Stocker la tranche d'âge ("18-25", "26-35", etc.)------------------------------------------------------
    fun setAgeRange(ageRange: UserAgeRange) {
        preferences.edit().putString(KEY_AGE_RANGE, ageRange.valeur).apply()
    }

    fun getAgeRange(): UserAgeRange {
        val value = preferences.getString(KEY_AGE_RANGE, null)
        return if (value != null) UserAgeRange.fromString(value) else UserAgeRange.IDLE
    }

    // Stocker la difficulté préférée ("débutant", "intermédiaire", "avancé")------------------------------------------------------
    fun setDifficulty(difficulty: UserDifficulty) {
        preferences.edit().putString(KEY_DIFFICULTY, difficulty.valeur).apply()
    }

    fun getDifficulty(): UserDifficulty {
        val value = preferences.getString(KEY_DIFFICULTY, null)
        return if (value != null) UserDifficulty.fromString(value) else UserDifficulty.IDLE
    }

    // Stocker l'url photo de l'utilisateur---------------------------------------------------
    fun setPhotoUrl(photo: String) {
        preferences.edit().putString(KEY_PHOTO, photo).apply()
    }

    fun getPhotoUrl(): String? {
        return preferences.getString(KEY_PHOTO, null)
    }

    // Stocker l'etat d'authentification------------------------------------------------------
    fun setIsLoggedIn(isLoggedIn: Boolean) {
        preferences.edit().putBoolean(KEY_IS_LOGGED_IN, isLoggedIn).apply()
    }

    fun isLoggedIn(): Boolean {
        return preferences.getBoolean(KEY_IS_LOGGED_IN, false)  // Par défaut, utilisateur non authentifié
    }

    // Effacer toutes les données utilisateur------------------------------------------------------
    // Sauf usertype (utilisé dans le splash)
    fun clearUserPreferences() {
        preferences.edit().apply {
            remove(KEY_USER_ID)       // Supprimer l'ID utilisateur
            remove(KEY_EMAIL)         // Supprimer l'adresse e-mail
            remove(KEY_FULL_NAME)     // Supprimer le nom complet
            remove(KEY_AGE_RANGE)     // Supprimer la tranche d'âge
            remove(KEY_DIFFICULTY)    // Supprimer la difficulté choisie
            putBoolean(KEY_IS_LOGGED_IN, false)  // Mettre l'état de connexion à false
        }.apply()
    }
}
