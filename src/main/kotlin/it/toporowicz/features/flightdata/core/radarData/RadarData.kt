package it.toporowicz.features.flightdata.core.radarData

import it.toporowicz.features.flightdata.core.coordinates.Distance

data class RadarData (
        val icao24: String,
        val callSign: String?,
        val barometricAltitude: Distance?,
        val onGround: Boolean?,
)