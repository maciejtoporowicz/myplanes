package it.toporowicz.features.flightdata.core.broadcast.notifications

interface NotificationSender {
    fun send(notification: Notification)
}