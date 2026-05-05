package ly.music.catalog.domain.release

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter(autoApply = true)
class ReleasedAtConverter : AttributeConverter<ReleasedAt, String> {
    override fun convertToDatabaseColumn(attribute: ReleasedAt?): String? = attribute?.value

    override fun convertToEntityAttribute(dbData: String?): ReleasedAt? = ReleasedAt.parseOrNull(dbData)
}
