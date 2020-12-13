package it.toporowicz.domain.radar.ports.broadcast.notifications

data class NewFlightsEvent (
    val jobId: String,
    val newFlightsCount: Int
)
