package ly.music.catalog.interfaces.rest

import org.springframework.data.web.HateoasPageableHandlerMethodArgumentResolver
import org.springframework.data.web.PagedResourcesAssembler

fun <T : Any> pagedAssembler(): PagedResourcesAssembler<T> = PagedResourcesAssembler(HateoasPageableHandlerMethodArgumentResolver(), null)
