package it.toporowicz.domain.radar.ports.radarData

import it.toporowicz.domain.radar.core.coordinates.CoordinatesBoundary
import it.toporowicz.domain.radar.api.Distance

interface RadarDataProvider {
    fun getRadarDataWithin(coordinatesBoundary: CoordinatesBoundary, maxAltitude: Distance): Set<RadarData>
}