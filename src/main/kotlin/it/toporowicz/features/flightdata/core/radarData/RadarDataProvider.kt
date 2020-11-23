package it.toporowicz.features.flightdata.core.radarData

import it.toporowicz.features.flightdata.core.coordinates.CoordinatesBoundary
import it.toporowicz.features.flightdata.core.coordinates.Distance

interface RadarDataProvider {
    fun getRadarDataWithin(coordinatesBoundary: CoordinatesBoundary, maxAltitude: Distance): Set<RadarData>
}