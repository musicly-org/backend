package ly.music.catalog.domain.release

class ReleasedAt private constructor(
    val value: String,
) {
    override fun toString(): String = value

    override fun equals(other: Any?): Boolean = other is ReleasedAt && value == other.value

    override fun hashCode(): Int = value.hashCode()

    companion object {
        private val allowedFormat = Regex("""^[0-9]{4}(-[0-9]{2}(-[0-9]{2})?)?$""")

        fun parse(value: String): ReleasedAt {
            val normalized = value.trim()
            require(allowedFormat.matches(normalized)) {
                "Released at must use YYYY, YYYY-MM, or YYYY-MM-DD format"
            }
            return ReleasedAt(normalized)
        }

        fun parseOrNull(value: String?): ReleasedAt? = value?.takeIf { it.isNotBlank() }?.let(::parse)
    }
}
