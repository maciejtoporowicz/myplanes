package it.toporowicz.features.flightdata.core.storage

import it.toporowicz.features.flightdata.core.LastKnownRadarData
import it.toporowicz.features.flightdata.core.radarData.RadarData

typealias Icao24OfNewFlights = Set<String>

interface FlightDataStorage {
    fun set(jobId: String, radarData: Set<RadarData>): Icao24OfNewFlights
    fun get(jobId: String): LastKnownRadarData?
}