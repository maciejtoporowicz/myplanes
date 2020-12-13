package it.toporowicz.application.radar

import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Produces
import it.toporowicz.domain.radar.api.LastKnownFlightData
import it.toporowicz.domain.radar.core.RadarModule

@Controller("/flightData")
class RadarDataController(private val radarModule: RadarModule) {
    @Get("/{jobId}")
    @Produces(MediaType.APPLICATION_JSON)
    fun getRadarDataFor(jobId: String): LastKnownFlightData? {
        return radarModule.queries().getLastKnownFlightDataFor(jobId)
    }
}