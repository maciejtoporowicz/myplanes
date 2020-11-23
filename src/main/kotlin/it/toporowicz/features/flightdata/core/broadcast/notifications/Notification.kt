package it.toporowicz.features.flightdata.core.broadcast.notifications

data class Notification (
    val jobId: String,
    val newFlightsCount: Int
)
