package it.toporowicz.radarData

import it.toporowicz.coordinates.CoordinatesBoundary
import it.toporowicz.coordinates.Distance

interface RadarDataProvider {
    fun getRadarDataWithin(coordinatesBoundary: CoordinatesBoundary, maxAltitude: Distance): Set<RadarData>
}