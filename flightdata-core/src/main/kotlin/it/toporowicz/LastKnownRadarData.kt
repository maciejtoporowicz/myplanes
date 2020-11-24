package it.toporowicz

import it.toporowicz.radarData.RadarData
import java.time.Instant

data class LastKnownRadarData(
        val jobId: String,
        val updatedAt: Instant,
        val radarData: Set<RadarData>
)