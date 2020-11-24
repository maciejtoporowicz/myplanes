package it.toporowicz

import it.toporowicz.aircrafts.AircraftDataProvider
import it.toporowicz.broadcast.FlightBroadcaster
import it.toporowicz.coordinates.CoordinatesBoundaryCreator
import it.toporowicz.radarData.RadarDataProvider
import it.toporowicz.storage.FlightDataStorage

class FlightDataModule(
        private val coordinatesBoundaryCreator: CoordinatesBoundaryCreator,
        private val radarDataProvider: RadarDataProvider,
        private val flightBroadcaster: FlightBroadcaster,
        private val flightDataStorage: FlightDataStorage,
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

        val icao24OfNewFlights = flightDataStorage.set(flightScannerConfig.jobId, freshRadarData)

        flightBroadcaster.broadcast(flightScannerConfig.jobId, icao24OfNewFlights)
    }

    fun getLastKnownFlightDataFor(jobId: String): LastKnownFlightsData? {
        val lastKnownRadarData = flightDataStorage.get(jobId) ?: return null

        val icao24s = lastKnownRadarData.radarData.map { it.icao24 }.toSet()

        val aircraftData = aircraftDataProvider.getDataBy(icao24s)

        return LastKnownFlightsData(lastKnownRadarData.jobId, lastKnownRadarData.updatedAt,
                lastKnownRadarData.radarData.map {
                    val matchingAircraftData = aircraftData.get(it.icao24)
                    FlightData(it.icao24, it.callSign, it.barometricAltitude, it.onGround, matchingAircraftData?.make, matchingAircraftData?.model, matchingAircraftData?.owner)
                }.toSet())
    }
}