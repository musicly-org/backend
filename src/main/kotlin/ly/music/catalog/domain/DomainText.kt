package ly.music.catalog.domain

fun normalizeRequiredText(
    value: String,
    field: String,
): String {
    val normalized = value.trim()
    require(normalized.isNotEmpty()) { "$field must not be blank" }
    return normalized
}
