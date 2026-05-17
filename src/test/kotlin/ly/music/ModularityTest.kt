package ly.music

import org.junit.jupiter.api.Test
import org.springframework.modulith.core.ApplicationModules

class ModularityTest {
    @Test
    fun applicationModules_shouldReturnVerified() {
        ApplicationModules.of(MusiclyBackendApplication::class.java).verify()
    }
}
