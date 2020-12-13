package it.toporowicz.domain.radar.api

import java.time.Instant

data class FlightData (
        val icao24: String,
        val callSign: String?,
        val barometricAltitude: Distance?,
        val onGround: Boolean?,
        val longitude: DecimalDegrees?,
        val latitude: DecimalDegrees?,
        val aircraftMake: String?,
        val aircraftModel: String?,
        val owner: String?
)

data class LastKnownFlightData(
        val jobId: String,
        val updatedAt: Instant,
        val data: List<FlightData>
)