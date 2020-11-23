package it.toporowicz.features.flightdata.core

import it.toporowicz.features.flightdata.core.coordinates.Coordinates
import it.toporowicz.features.flightdata.core.coordinates.Distance

data class FlightScannerConfig (
        val jobId: String,
        val coordinates: Coordinates,
        val boundaryOffsetNorth: Distance,
        val boundaryOffsetEast: Distance,
        val boundaryOffsetSouth: Distance,
        val boundaryOffsetWest: Distance,
        val altitudeThreshold: Distance
)