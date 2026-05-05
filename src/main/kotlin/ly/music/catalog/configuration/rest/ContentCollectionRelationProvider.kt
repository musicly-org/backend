package ly.music.catalog.configuration.rest

import org.springframework.core.Ordered
import org.springframework.hateoas.LinkRelation
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.LinkRelationProvider
import org.springframework.stereotype.Component

@Component
class ContentCollectionRelationProvider : LinkRelationProvider, Ordered {
    override fun getItemResourceRelFor(type: Class<*>): LinkRelation =
        throw UnsupportedOperationException("ContentCollectionRelationProvider only supports collection relations.")

    override fun getCollectionResourceRelFor(type: Class<*>): LinkRelation = LinkRelation.of("content")

    override fun supports(delimiter: LinkRelationProvider.LookupContext): Boolean =
        delimiter.isCollectionRelationLookup &&
            RepresentationModel::class.java.isAssignableFrom(delimiter.type) &&
            delimiter.type.`package`.name.startsWith("ly.music.catalog.interfaces.rest")

    override fun getOrder(): Int = Ordered.HIGHEST_PRECEDENCE
}
