package it.toporowicz.delivery.flightdata.rest

import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Produces
import it.toporowicz.features.flightdata.core.FlightDataModule
import it.toporowicz.features.flightdata.core.LastKnownFlightsData

@Controller("/flightData")
class FlightDataController(private val flightDataModule: FlightDataModule) {
    @Get("/{jobId}")
    @Produces(MediaType.APPLICATION_JSON)
    fun getRadarDataFor(jobId: String): LastKnownFlightsData? {
        return flightDataModule.getLastKnownFlightDataFor(jobId)
    }
}