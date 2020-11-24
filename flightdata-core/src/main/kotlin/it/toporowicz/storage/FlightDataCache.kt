package it.toporowicz.storage

import it.toporowicz.LastKnownRadarData
import it.toporowicz.radarData.RadarData

interface FlightDataCache {
    fun set(jobId: String, radarData: Set<RadarData>)
    fun get(jobId: String): LastKnownRadarData?
}