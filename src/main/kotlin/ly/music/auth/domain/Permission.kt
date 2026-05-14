package ly.music.auth.domain

enum class Permission(
    val authority: String,
) {
    CATALOG_READ("catalog:read"),
    CATALOG_WRITE("catalog:write"),
    ADMIN_MANAGE("admin:manage"),
}
