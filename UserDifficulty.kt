package mg.gasydev.tenymalagasy.domain.enumType

enum class UserDifficulty(val valeur: String) {
    IDLE("idle"),
    DEBUTANT("débutant"),
    INTERMEDIAIRE("intermédiaire"),
    AVANCE("avancé");

    companion object {
        fun fromString(value: String): UserDifficulty {
            return entries.find { it.valeur == value }
                ?: throw IllegalArgumentException("Aucune difficulté correspondante pour la valeur: $value")
        }
    }
}