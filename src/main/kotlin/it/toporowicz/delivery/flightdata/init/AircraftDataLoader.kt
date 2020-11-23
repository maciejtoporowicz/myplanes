package it.toporowicz.delivery.flightdata.init

import io.micronaut.context.annotation.ConfigurationProperties
import io.micronaut.context.annotation.Context
import it.toporowicz.infrastructure.db.AircraftData
import it.toporowicz.infrastructure.db.RedisBasedAircraftRepo
import org.slf4j.LoggerFactory
import java.io.File
import javax.annotation.PostConstruct
import kotlin.concurrent.thread

@ConfigurationProperties("aircraft-data-file")
interface AircraftDataFileConfig {
    val path: String
}

@Context
class AircraftDataLoader(private val redisBasedAircraftRepo: RedisBasedAircraftRepo,
                         private val aircraftDataFileConfig: AircraftDataFileConfig) {
    companion object {
        val log = LoggerFactory.getLogger(AircraftDataLoader::class.java)
    }

    @PostConstruct
    fun init() {
        thread(start = true) {
            log.info("Loading aircraft data...")
            File(aircraftDataFileConfig.path).forEachLine {
                val values = it.split(",")

                if (values.size != 4) {
                    return@forEachLine
                }

                val icao24 = values[0]

                if (icao24.isEmpty()) {
                    return@forEachLine
                }

                val make = values[1]
                val model = values[2]
                val owner = values[3]

                redisBasedAircraftRepo.putData(AircraftData(icao24, make, model, owner))
            }
            log.info("Finished loading aircraft data...")
        }
    }
}