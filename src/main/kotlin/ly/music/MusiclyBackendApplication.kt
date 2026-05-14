package ly.music

import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.boot.runApplication
import org.springframework.modulith.Modulithic

@Modulithic
@SpringBootApplication
@EnableCaching
@ConfigurationPropertiesScan
class MusiclyBackendApplication

fun main(args: Array<String>) {
    runApplication<MusiclyBackendApplication>(*args)
}
