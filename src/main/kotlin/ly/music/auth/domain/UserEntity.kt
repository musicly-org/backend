package ly.music.auth.domain

import jakarta.persistence.CollectionTable
import jakarta.persistence.Column
import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.Table

@Entity
@Table(schema = "auth", name = "users")
class UserEntity(
    @Column(name = "email", nullable = false)
    var email: String,
    @Column(name = "password_hash", nullable = false)
    var passwordHash: String,
    @Column(name = "display_name")
    var displayName: String? = null,
    @Column(name = "enabled", nullable = false)
    var enabled: Boolean = true,
) : AuthBaseEntity() {
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        schema = "auth",
        name = "user_roles",
        joinColumns = [JoinColumn(name = "user_id")],
    )
    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    val roles: MutableSet<UserRole> = mutableSetOf()

    val permissions: Set<Permission>
        get() = roles.flatMapTo(linkedSetOf()) { it.permissions }
}
