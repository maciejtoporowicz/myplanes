package it.toporowicz.domain.flightdata.ports.cache

import it.toporowicz.domain.flightdata.ports.radarData.RadarData
import java.time.Instant

data class LastKnownRadarData(
        val jobId: String,
        val updatedAt: Instant,
        val radarData: Set<RadarData>
)