package it.toporowicz

import it.toporowicz.aircrafts.AircraftDataProvider
import it.toporowicz.broadcast.FlightBroadcaster
import it.toporowicz.coordinates.CoordinatesBoundaryCreator
import it.toporowicz.radarData.RadarDataProvider
import it.toporowicz.storage.FlightDataCache

class FlightDataModule(
        private val coordinatesBoundaryCreator: CoordinatesBoundaryCreator,
        private val radarDataProvider: RadarDataProvider,
        private val flightBroadcaster: FlightBroadcaster,
        private val flightDataCache: FlightDataCache,
        private val aircraftDataProvider: AircraftDataProvider
) {
    fun broadcastDataAccordingTo(flightScannerConfig: FlightScannerConfig) {
        val coordinatesBoundary = coordinatesBoundaryCreator.create(
                flightScannerConfig.coordinates,
                flightScannerConfig.boundaryOffsetNorth,
                flightScannerConfig.boundaryOffsetEast,
                flightScannerConfig.boundaryOffsetSouth,
                flightScannerConfig.boundaryOffsetWest
        )

        val freshRadarData = radarDataProvider.getRadarDataWithin(coordinatesBoundary, flightScannerConfig.altitudeThreshold)

        val oldRadarData = flightDataCache.get(flightScannerConfig.jobId)
        flightDataCache.set(flightScannerConfig.jobId, freshRadarData)

        val icao24OfNewFlights = freshRadarData
                .map { it.icao24 }
                .minus(oldRadarData?.radarData?.map { it.icao24 } ?: emptySet())
                .toSet()

        flightBroadcaster.broadcast(flightScannerConfig.jobId, icao24OfNewFlights)
    }

    fun getLastKnownFlightDataFor(jobId: String): LastKnownFlightsData? {
        val lastKnownRadarData = flightDataCache.get(jobId) ?: return null

        val icao24s = lastKnownRadarData.radarData.map { it.icao24 }.toSet()

        val aircraftData = aircraftDataProvider.getDataBy(icao24s)

        return LastKnownFlightsData(lastKnownRadarData.jobId, lastKnownRadarData.updatedAt,
                lastKnownRadarData.radarData.map {
                    val matchingAircraftData = aircraftData.get(it.icao24)
                    FlightData(it.icao24, it.callSign, it.barometricAltitude, it.onGround, matchingAircraftData?.make, matchingAircraftData?.model, matchingAircraftData?.owner)
                }.toSet())
    }
}