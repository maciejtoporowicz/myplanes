package it.toporowicz.domain.flightdata.core

import it.toporowicz.domain.flightdata.api.*
import it.toporowicz.domain.flightdata.core.broadcast.FlightBroadcaster
import it.toporowicz.domain.flightdata.core.coordinates.CoordinatesBoundaryCreator
import it.toporowicz.domain.flightdata.ports.aircrafts.AircraftDataRepo
import it.toporowicz.domain.flightdata.ports.cache.FlightDataCache
import it.toporowicz.domain.flightdata.ports.radarData.RadarData
import it.toporowicz.domain.flightdata.ports.radarData.RadarDataProvider

class FlightDataModule(
        private val coordinatesBoundaryCreator: CoordinatesBoundaryCreator,
        private val radarDataProvider: RadarDataProvider,
        private val flightBroadcaster: FlightBroadcaster,
        private val flightDataCache: FlightDataCache,
        private val aircraftDataRepo: AircraftDataRepo
) {
    fun broadcastDataAccordingTo(flightScannerConfig: FlightScannerConfig) {
        val coordinatesBoundary = coordinatesBoundaryCreator.create(
                flightScannerConfig.coordinates,
                flightScannerConfig.boundaryOffsetNorth,
                flightScannerConfig.boundaryOffsetEast,
                flightScannerConfig.boundaryOffsetSouth,
                flightScannerConfig.boundaryOffsetWest
        )

        val oldRadarData = flightDataCache.get(flightScannerConfig.jobId)
        val freshRadarData = radarDataProvider.getRadarDataWithin(coordinatesBoundary, flightScannerConfig.altitudeThreshold)

        flightDataCache.set(flightScannerConfig.jobId, freshRadarData)

        val icao24OfFreshFlights = freshRadarData.map { it.icao24 }.toSet()
        val icao24OfOldFlights = oldRadarData?.radarData?.map { it.icao24 }?.toSet() ?: emptySet()

        val icao24OfNewlyAddedFlights = icao24OfFreshFlights - icao24OfOldFlights

        flightBroadcaster.broadcast(flightScannerConfig.jobId, icao24OfNewlyAddedFlights)
    }

    fun getLastKnownFlightDataFor(jobId: String): LastKnownFlightsData? {
        val lastKnownRadarData = flightDataCache.get(jobId) ?: return null

        val icao24s = lastKnownRadarData.radarData.map { it.icao24 }.toSet()

        val aircraftData = aircraftDataRepo.getDataBy(icao24s)

        return LastKnownFlightsData(lastKnownRadarData.jobId, lastKnownRadarData.updatedAt,
                lastKnownRadarData.radarData.map {
                    val matchingAircraftData = aircraftData.get(it.icao24)
                    FlightData(it.icao24, it.callSign, it.barometricAltitude, it.onGround, matchingAircraftData?.make, matchingAircraftData?.model, matchingAircraftData?.owner)
                }.sortedBy { it.icao24 })
    }

    fun putAircraftData(aircraftData: AircraftData) {
        aircraftDataRepo.putData(aircraftData)
    }
}