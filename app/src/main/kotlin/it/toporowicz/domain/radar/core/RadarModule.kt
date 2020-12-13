package it.toporowicz.domain.radar.core

import it.toporowicz.domain.radar.api.AircraftData
import it.toporowicz.domain.radar.ports.aircraft.AircraftDataRepo
import it.toporowicz.domain.radar.api.*
import it.toporowicz.domain.radar.core.broadcast.EventBroadcaster
import it.toporowicz.domain.radar.core.coordinates.CoordinatesBoundaryCreator
import it.toporowicz.domain.radar.ports.cache.FlightDataCache
import it.toporowicz.domain.radar.ports.radarData.RadarDataProvider

class RadarModule(
        private val coordinatesBoundaryCreator: CoordinatesBoundaryCreator,
        private val radarDataProvider: RadarDataProvider,
        private val eventBroadcaster: EventBroadcaster,
        private val flightDataCache: FlightDataCache,
        private val aircraftDataRepo: AircraftDataRepo,
        private val queries: RadarQueries
) {
    fun cacheRadarDataAccordingTo(radarConfig: RadarConfig) {
        val coordinatesBoundary = coordinatesBoundaryCreator.create(
                radarConfig.coordinates,
                radarConfig.boundaryOffsetNorth,
                radarConfig.boundaryOffsetEast,
                radarConfig.boundaryOffsetSouth,
                radarConfig.boundaryOffsetWest
        )

        val oldRadarData = flightDataCache.get(radarConfig.jobId)
        val freshRadarData = radarDataProvider.getRadarDataWithin(coordinatesBoundary, radarConfig.altitudeThreshold)

        flightDataCache.set(radarConfig.jobId, freshRadarData)

        val icao24OfFreshFlights = freshRadarData.map { it.icao24 }.toSet()
        val icao24OfOldFlights = oldRadarData?.radarData?.map { it.icao24 }?.toSet() ?: emptySet()

        val icao24OfNewlyAddedFlights = icao24OfFreshFlights - icao24OfOldFlights

        eventBroadcaster.broadcastEventFor(radarConfig.jobId, icao24OfNewlyAddedFlights)
    }

    fun addAircraftData(aircraftData: AircraftData) {
        aircraftDataRepo.putData(aircraftData)
    }

    fun queries(): RadarQueries {
        return queries
    }
}