package it.toporowicz.domain.radar.api

data class RadarConfig (
        val jobId: String,
        val coordinates: Coordinates,
        val boundaryOffsetNorth: Distance,
        val boundaryOffsetEast: Distance,
        val boundaryOffsetSouth: Distance,
        val boundaryOffsetWest: Distance,
        val altitudeThreshold: Distance
)