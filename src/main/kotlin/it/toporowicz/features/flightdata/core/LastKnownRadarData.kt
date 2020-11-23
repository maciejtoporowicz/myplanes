package it.toporowicz.features.flightdata.core

import it.toporowicz.features.flightdata.core.radarData.RadarData
import java.time.Instant

data class LastKnownRadarData(
        val jobId: String,
        val updatedAt: Instant,
        val radarData: Set<RadarData>
)