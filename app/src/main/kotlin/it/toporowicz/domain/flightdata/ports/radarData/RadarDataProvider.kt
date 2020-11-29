package it.toporowicz.domain.flightdata.ports.radarData

import it.toporowicz.domain.flightdata.core.coordinates.CoordinatesBoundary
import it.toporowicz.domain.flightdata.api.Distance

interface RadarDataProvider {
    fun getRadarDataWithin(coordinatesBoundary: CoordinatesBoundary, maxAltitude: Distance): Set<RadarData>
}