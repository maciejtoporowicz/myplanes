package it.toporowicz.storage

import it.toporowicz.LastKnownRadarData
import it.toporowicz.radarData.RadarData

typealias Icao24OfNewFlights = Set<String>

interface FlightDataStorage {
    fun set(jobId: String, radarData: Set<RadarData>): Icao24OfNewFlights
    fun get(jobId: String): LastKnownRadarData?
}