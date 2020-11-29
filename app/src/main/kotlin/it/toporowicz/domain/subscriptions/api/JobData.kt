package it.toporowicz.domain.subscriptions.api

data class JobData(
        val jobId: String,
        val name: String,
        val coordinates: String,
        val boundaryOffsetNorth: String,
        val boundaryOffsetEast: String,
        val boundaryOffsetSouth: String,
        val boundaryOffsetWest: String,
        val altitudeThreshold: String
)