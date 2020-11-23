package it.toporowicz.features.flightdata.core.broadcast

import it.toporowicz.features.flightdata.core.broadcast.notifications.Notification
import it.toporowicz.features.flightdata.core.broadcast.notifications.NotificationSender
import org.slf4j.LoggerFactory

class FlightBroadcaster(
        private val notificationSender: NotificationSender
) {
    companion object {
        private val log = LoggerFactory.getLogger(FlightBroadcaster::class.java)
    }

    fun broadcast(jobId: String, icao24OfNewFlights: Set<String>) {
        if (icao24OfNewFlights.isEmpty()) {
            log.info("No flight data found.")
            return
        }

        notificationSender.send(Notification(jobId, icao24OfNewFlights.size))
    }
}