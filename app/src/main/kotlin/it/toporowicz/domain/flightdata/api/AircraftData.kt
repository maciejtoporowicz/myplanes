package it.toporowicz.domain.flightdata.api

data class AircraftData (
        val icao24: Icao24,
        val make: String?,
        val model: String?,
        val owner: String?
)