package it.toporowicz.domain.radar.core.broadcast

import it.toporowicz.domain.radar.ports.broadcast.notifications.NewFlightsEvent
import it.toporowicz.domain.radar.ports.broadcast.notifications.NewFlightEventSender
import org.slf4j.LoggerFactory

class EventBroadcaster(
        private val newFlightsEventSender: NewFlightEventSender
) {
    companion object {
        private val log = LoggerFactory.getLogger(EventBroadcaster::class.java)
    }

    fun broadcastEventFor(jobId: String, icao24OfNewFlights: Set<String>) {
        if (icao24OfNewFlights.isEmpty()) {
            log.info("No flight data found.")
            return
        }

        newFlightsEventSender.send(NewFlightsEvent(jobId, icao24OfNewFlights.size))
    }
}