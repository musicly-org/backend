package ly.music.catalog.interfaces.rest.shared

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode

abstract class LinkRequest {
    @field:JsonProperty("_links")
    val links: JsonNode? = null

    protected fun requiredLink(name: String): RequestLink =
        links
            ?.get(name)
            ?.get("href")
            ?.asText()
            ?.let(::RequestLink)
            ?: error("Missing _links.$name")

    protected fun requiredLinkList(name: String): List<RequestLink> =
        links
            ?.get(name)
            ?.mapNotNull { node -> node.get("href")?.asText()?.let(::RequestLink) }
            ?.takeIf { it.isNotEmpty() }
            ?: error("Missing _links.$name")
}
