package it.toporowicz.domain.radar.core.coordinates

import it.toporowicz.domain.radar.api.Coordinates
import it.toporowicz.domain.radar.api.DecimalDegrees

data class CoordinatesBoundary(
        val latitudeMax: DecimalDegrees,
        val longitudeMax: DecimalDegrees,
        val latitudeMin: DecimalDegrees,
        val longitudeMin: DecimalDegrees
) {
    fun contains(coordinates: Coordinates): Boolean {
        val latitude = coordinates.latitude
        val longitude = coordinates.longitude

        return latitude.value <= latitudeMax.value && latitude.value >= latitudeMin.value
                && longitude.value <= longitudeMax.value && longitude.value >= longitudeMin.value
    }
}