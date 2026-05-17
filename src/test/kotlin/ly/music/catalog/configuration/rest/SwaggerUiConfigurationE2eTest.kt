package ly.music.catalog.configuration.rest

import ly.music.catalog.BackendControllerE2eTestSupport
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class SwaggerUiConfigurationE2eTest : BackendControllerE2eTestSupport() {
    @Test
    fun trailingSlashSwaggerUiPath_shouldReturnRedirect() {
        mockMvc.perform(get("http://localhost:8080/swagger-ui/"))
            .andExpect(status().is3xxRedirection)
            .andExpect(header().string(HttpHeaders.LOCATION, "/swagger-ui/index.html"))
    }

    @Test
    fun openApiJson_shouldBeServedFromPublishedPath() {
        mockMvc.perform(get("http://localhost:8080/openapi"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.openapi").exists())
    }
}
