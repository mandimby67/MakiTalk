package mg.gasydev.tenymalagasy.domain.enumType

enum class UserAgeRange(val valeur: String) {
    IDLE("idle"),
    ENFANT("6-12"),
    ADOLESCENT("13-18"),
    ADULTE("18+");

    companion object {
        fun fromString(value: String): UserAgeRange {
            return UserAgeRange.entries.find { it.valeur == value }
                ?: throw IllegalArgumentException("Aucune trache age correspondante pour la valeur: $value")
        }
    }
}