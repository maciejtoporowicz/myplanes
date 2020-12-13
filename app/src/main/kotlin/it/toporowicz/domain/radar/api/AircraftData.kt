package it.toporowicz.domain.radar.api

import it.toporowicz.domain.radar.api.Icao24

data class AircraftData (
        val icao24: Icao24,
        val make: String?,
        val model: String?,
        val owner: String?
)