package it.toporowicz.domain.radar.ports.cache

import it.toporowicz.domain.radar.ports.radarData.RadarData

interface FlightDataCache {
    fun set(jobId: String, radarData: Set<RadarData>)
    fun get(jobId: String): LastKnownRadarData?
}