package ly.music.auth.domain

enum class UserRole(
    val permissions: Set<Permission>,
) {
    REGULAR_USER(
        permissions =
            setOf(
                Permission.CATALOG_READ,
            ),
    ),
    SUPER_ADMIN(
        permissions =
            setOf(
                Permission.CATALOG_READ,
                Permission.CATALOG_WRITE,
                Permission.ADMIN_MANAGE,
            ),
    ),
}
