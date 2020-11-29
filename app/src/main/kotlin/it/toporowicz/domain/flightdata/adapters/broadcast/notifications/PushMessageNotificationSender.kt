package it.toporowicz.domain.flightdata.adapters.broadcast.notifications

import it.toporowicz.domain.flightdata.ports.broadcast.notifications.Notification
import it.toporowicz.domain.flightdata.ports.broadcast.notifications.NotificationSender
import it.toporowicz.infrastructure.pushmessaging.Message
import it.toporowicz.infrastructure.pushmessaging.PushMessagingService
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class PushMessageNotificationSender(
        private val pushMessagingService: PushMessagingService
) : NotificationSender {
    companion object {
        val log: Logger = LoggerFactory.getLogger(this::class.java)
    }

    override fun send(notification: Notification) {
        log.info("Sending notification: $notification to topic=[${notification.jobId}]")

        val topic = notification.jobId

        pushMessagingService.sendMessageToTopic(
                topic,
                Message(mapOf(
                        "jobId" to notification.jobId,
                        "newFlightsCount" to notification.newFlightsCount.toString()
                ))
        )
    }
}