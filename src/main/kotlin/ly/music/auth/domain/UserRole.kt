package ly.music.auth.domain

enum class UserRole(
    val permissions: Set<Permission>,
) {
    SUPER_ADMIN(
        permissions =
            setOf(
                Permission.CATALOG_READ,
                Permission.CATALOG_WRITE,
                Permission.ADMIN_MANAGE,
            ),
    ),
}
