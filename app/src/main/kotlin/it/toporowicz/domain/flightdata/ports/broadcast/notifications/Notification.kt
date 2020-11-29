package it.toporowicz.domain.flightdata.ports.broadcast.notifications

data class Notification (
    val jobId: String,
    val newFlightsCount: Int
)
