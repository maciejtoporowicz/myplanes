package it.toporowicz.domain.flightdata.api

data class FlightScannerConfig (
        val jobId: String,
        val coordinates: Coordinates,
        val boundaryOffsetNorth: Distance,
        val boundaryOffsetEast: Distance,
        val boundaryOffsetSouth: Distance,
        val boundaryOffsetWest: Distance,
        val altitudeThreshold: Distance
)