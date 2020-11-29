package it.toporowicz.domain.flightdata.api

import java.time.Instant

data class FlightData (
        val icao24: String,
        val callSign: String?,
        val barometricAltitude: Distance?,
        val onGround: Boolean?,
        val aircraftMake: String?,
        val aircraftModel: String?,
        val owner: String?
)

data class LastKnownFlightsData(
        val jobId: String,
        val updatedAt: Instant,
        val data: List<FlightData>
)