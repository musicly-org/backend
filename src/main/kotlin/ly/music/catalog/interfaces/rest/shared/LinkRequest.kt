package ly.music.catalog.interfaces.rest.shared

import com.fasterxml.jackson.annotation.JsonProperty

abstract class LinkRequest {
    @field:JsonProperty("_links")
    var links: Map<String, Any?>? = null

    protected fun hasLink(name: String): Boolean = links?.containsKey(name) == true

    protected fun requiredLink(name: String): RequestLink =
        (links?.get(name) as? Map<*, *>)
            ?.get("href")
            ?.toString()
            ?.let(::RequestLink)
            ?: throw IllegalArgumentException("Missing _links.$name")

    protected fun optionalLink(name: String): RequestLink? {
        if (!hasLink(name)) {
            return null
        }

        val href =
            (links?.get(name) as? Map<*, *>)
                ?.get("href")
                ?.toString()
                ?.takeIf { it.isNotBlank() }
                ?: throw IllegalArgumentException("Invalid _links.$name")
        return RequestLink(href)
    }

    protected fun requiredLinkList(name: String): List<RequestLink> =
        (links?.get(name) as? List<*>)
            ?.takeIf { it.isNotEmpty() }
            ?.map { node ->
                val href =
                    (node as? Map<*, *>)
                        ?.get("href")
                        ?.toString()
                        ?.takeIf { it.isNotBlank() }
                        ?: throw IllegalArgumentException("Invalid _links.$name")
                RequestLink(href)
            }
            ?: throw IllegalArgumentException("Missing _links.$name")
}
