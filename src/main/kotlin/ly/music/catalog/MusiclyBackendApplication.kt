package ly.music.catalog

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableCaching
class MusiclyBackendApplication

fun main(args: Array<String>) {
    runApplication<MusiclyBackendApplication>(*args)
}
