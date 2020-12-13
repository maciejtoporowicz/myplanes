package it.toporowicz.domain.radar.ports.cache

import it.toporowicz.domain.radar.ports.radarData.RadarData
import java.time.Instant

data class LastKnownRadarData(
        val jobId: String,
        val updatedAt: Instant,
        val radarData: Set<RadarData>
)