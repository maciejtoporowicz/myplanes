package it.toporowicz.domain.radar.core

import it.toporowicz.domain.radar.ports.aircraft.AircraftDataRepo
import it.toporowicz.domain.radar.api.FlightData
import it.toporowicz.domain.radar.api.LastKnownFlightsData
import it.toporowicz.domain.radar.ports.cache.FlightDataCache

class RadarQueries(private val flightDataCache: FlightDataCache, private val aircraftDataRepo: AircraftDataRepo) {
    fun getLastKnownRadarDataFor(jobId: String): LastKnownFlightsData? {
        val lastKnownRadarData = flightDataCache.get(jobId) ?: return null

        val icao24s = lastKnownRadarData.radarData.map { it.icao24 }.toSet()

        val aircraftData = aircraftDataRepo.getDataBy(icao24s)

        val radarData = lastKnownRadarData.radarData.map {
            val matchingAircraftData = aircraftData[it.icao24]
            FlightData(
                    it.icao24,
                    it.callSign,
                    it.barometricAltitude,
                    it.onGround,
                    it.longitude,
                    it.latitude,
                    matchingAircraftData?.make,
                    matchingAircraftData?.model,
                    matchingAircraftData?.owner
            )
        }

        return LastKnownFlightsData(
                lastKnownRadarData.jobId,
                lastKnownRadarData.updatedAt,
                radarData.sortedBy { it.icao24 }
        )
    }
}