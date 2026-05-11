package ly.music

import org.junit.jupiter.api.Test
import org.springframework.modulith.core.ApplicationModules

class ModularityTest {
    @Test
    fun verifiesApplicationModules() {
        ApplicationModules.of(MusiclyBackendApplication::class.java).verify()
    }
}
