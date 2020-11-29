package it.toporowicz.domain.flightdata.ports.radarData

import it.toporowicz.domain.flightdata.api.Distance

data class RadarData (
        val icao24: String,
        val callSign: String?,
        val barometricAltitude: Distance?,
        val onGround: Boolean?,
)