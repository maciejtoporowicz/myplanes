package it.toporowicz.domain.radar.ports.radarData

import it.toporowicz.domain.radar.api.DecimalDegrees
import it.toporowicz.domain.radar.api.Distance
import it.toporowicz.domain.radar.api.Track

data class RadarData (
        val icao24: String,
        val callSign: String?,
        val barometricAltitude: Distance?,
        val onGround: Boolean?,
        val longitude: DecimalDegrees?,
        val latitude: DecimalDegrees?,
        val track: Track?
)