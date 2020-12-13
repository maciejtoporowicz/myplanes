package it.toporowicz.application.radar

import io.micronaut.context.event.ApplicationEventListener
import it.toporowicz.domain.radar.ports.broadcast.notifications.NewFlightsEvent
import it.toporowicz.infrastructure.pushmessaging.Message
import it.toporowicz.infrastructure.pushmessaging.PushMessagingService
import javax.inject.Singleton

@Singleton
class NewFlightsEventHandler(private val pushMessagingService: PushMessagingService) : ApplicationEventListener<NewFlightsEvent> {
    override fun onApplicationEvent(event: NewFlightsEvent) {
        val topic = event.jobId
        val message = Message(mapOf(
                "jobId" to event.jobId,
                "newFlightsCount" to event.newFlightsCount.toString()
        ))

        pushMessagingService.sendMessageToTopic(topic, message)
    }
}