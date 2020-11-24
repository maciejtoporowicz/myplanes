package it.toporowicz.radarData

import it.toporowicz.coordinates.Distance

data class RadarData (
        val icao24: String,
        val callSign: String?,
        val barometricAltitude: Distance?,
        val onGround: Boolean?,
)