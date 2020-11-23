package it.toporowicz.features.flightdata.core.aircrafts

data class AircraftData (
        val icao24: String,
        val make: String?,
        val model: String?,
        val owner: String?
)