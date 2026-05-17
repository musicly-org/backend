package ly.music.catalog.interfaces.rest.shared

import java.net.URI
import java.util.UUID

data class RequestLink(
    val href: String,
) {
    fun uuid(): UUID = UUID.fromString(URI.create(href).path.substringAfterLast('/'))
}
