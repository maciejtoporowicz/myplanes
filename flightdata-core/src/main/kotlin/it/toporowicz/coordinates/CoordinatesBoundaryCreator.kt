package it.toporowicz.coordinates

import java.math.BigDecimal

val BEARING_NORTH = DecimalDegrees(BigDecimal(0))
val BEARING_EAST = DecimalDegrees(BigDecimal(90))
val BEARING_SOUTH = DecimalDegrees(BigDecimal(180))
val BEARING_WEST = DecimalDegrees(BigDecimal(270))

class CoordinatesBoundaryCreator {
    fun create(coordinates: Coordinates,
               boundaryOffsetNorth: Distance,
               boundaryOffsetEast: Distance,
               boundaryOffsetSouth: Distance,
               boundaryOffsetWest: Distance
    ): CoordinatesBoundary {
        val latitudeA = coordinates.addDistance(boundaryOffsetNorth,
            BEARING_NORTH
        ).latitude
        val latitudeB = coordinates.addDistance(boundaryOffsetSouth,
            BEARING_SOUTH
        ).latitude
        val longitudeA = coordinates.addDistance(boundaryOffsetEast,
            BEARING_EAST
        ).longitude
        val longitudeB = coordinates.addDistance(boundaryOffsetWest,
            BEARING_WEST
        ).longitude

        return CoordinatesBoundary(
            latitudeA,
            longitudeA,
            latitudeB,
            longitudeB
        )
    }
}