package it.toporowicz.domain.radar.adapters.broadcast.notifications

import io.micronaut.context.event.ApplicationEventPublisher
import it.toporowicz.domain.radar.ports.broadcast.notifications.NewFlightsEvent
import it.toporowicz.domain.radar.ports.broadcast.notifications.NewFlightEventSender

class PushMessageNewFlightEventSender(
        private val applicationEventPublisher: ApplicationEventPublisher
) : NewFlightEventSender {
    override fun send(newFlightsEvent: NewFlightsEvent) {
        applicationEventPublisher.publishEvent(newFlightsEvent)
    }
}