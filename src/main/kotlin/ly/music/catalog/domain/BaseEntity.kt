package ly.music.catalog.domain

import jakarta.persistence.Column
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import org.hibernate.Hibernate
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.util.UUID

@MappedSuperclass
abstract class BaseEntity(
    @Id
    @Column(name = "id", updatable = false)
    val id: UUID = UUID.randomUUID(),
) {
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    var createdAt: Instant? = null

    @UpdateTimestamp
    @Column(name = "updated_at")
    var updatedAt: Instant? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        if (Hibernate.getClass(this) != Hibernate.getClass(other)) return false

        other as BaseEntity
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}
