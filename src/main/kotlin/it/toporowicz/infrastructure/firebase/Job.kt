package it.toporowicz.infrastructure.firebase

data class Job (
        val jobId: String,
        val name: String,
        val coordinates: String,
        val boundaryOffsetNorth: String,
        val boundaryOffsetEast: String,
        val boundaryOffsetSouth: String,
        val boundaryOffsetWest: String,
        val altitudeThreshold: String
)