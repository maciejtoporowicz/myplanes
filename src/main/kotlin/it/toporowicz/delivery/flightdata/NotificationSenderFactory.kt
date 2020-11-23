package it.toporowicz.delivery.flightdata

import com.google.firebase.messaging.FirebaseMessaging
import io.micronaut.context.annotation.Factory
import it.toporowicz.features.flightdata.adapter.broadcast.notifications.FirebaseBasedNotificationSender
import javax.inject.Singleton

@Factory
internal class NotificationSenderFactory {
    @Singleton
    fun create(firebaseMessaging: FirebaseMessaging): FirebaseBasedNotificationSender {
        return FirebaseBasedNotificationSender(firebaseMessaging)
    }
}