package it.toporowicz.startup

import io.micronaut.context.annotation.ConfigurationProperties
import io.micronaut.context.annotation.Context
import it.toporowicz.domain.radar.core.RadarModule
import it.toporowicz.domain.radar.api.AircraftData
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import javax.annotation.PostConstruct
import kotlin.concurrent.thread

@ConfigurationProperties("aircraft-data-file")
interface AircraftDataFileConfig {
    val path: String?
}

@Context
class AircraftDataLoader(private val radarModule: RadarModule,
                         private val aircraftDataFileConfig: AircraftDataFileConfig) {
    companion object {
        val log: Logger = LoggerFactory.getLogger(AircraftDataLoader::class.java)
    }

    @PostConstruct
    fun init() {
        thread(start = true) {
            val aircraftDataFileConfigPath = aircraftDataFileConfig.path

            if(aircraftDataFileConfigPath == null) {
                log.warn("Path to aircraft data file not provided.")
                return@thread
            }

            val file = File(aircraftDataFileConfigPath)

            if(!file.exists()) {
                log.warn("Aircraft data file does not exist.")
                return@thread
            }

            log.info("Loading aircraft data...")

            file.forEachLine {
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

                radarModule.addAircraftData(AircraftData(icao24, make, model, owner))
            }

            log.info("Finished loading aircraft data...")
        }
    }
}