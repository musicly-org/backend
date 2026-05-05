package ly.music.catalog.application

import java.util.UUID

class NotFoundException(resource: String, id: UUID) : RuntimeException("$resource not found: $id")
