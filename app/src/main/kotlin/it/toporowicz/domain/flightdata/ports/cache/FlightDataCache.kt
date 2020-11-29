package it.toporowicz.domain.flightdata.ports.cache

import it.toporowicz.domain.flightdata.ports.radarData.RadarData

interface FlightDataCache {
    fun set(jobId: String, radarData: Set<RadarData>)
    fun get(jobId: String): LastKnownRadarData?
}