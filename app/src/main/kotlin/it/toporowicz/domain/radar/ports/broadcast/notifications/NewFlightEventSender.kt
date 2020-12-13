package it.toporowicz.domain.radar.ports.broadcast.notifications

interface NewFlightEventSender {
    fun send(newFlightsEvent: NewFlightsEvent)
}