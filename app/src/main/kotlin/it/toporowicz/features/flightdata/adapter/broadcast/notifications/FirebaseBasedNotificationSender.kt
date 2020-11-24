package it.toporowicz.features.flightdata.adapter.broadcast.notifications

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import it.toporowicz.broadcast.notifications.Notification
import it.toporowicz.broadcast.notifications.NotificationSender
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class FirebaseBasedNotificationSender(
    private val messaging: FirebaseMessaging,
) : NotificationSender {
    companion object {
        val log: Logger = LoggerFactory.getLogger(this::class.java)
    }

    override fun send(notification: Notification) {
        log.info("Sending notification: $notification to topic=[${notification.jobId}]")

        messaging.send(
            Message.builder()
                .setTopic(notification.jobId)
                .putData("jobId", notification.jobId)
                .putData("newFlightsCount", notification.newFlightsCount.toString())
                .build()
        )
    }
}