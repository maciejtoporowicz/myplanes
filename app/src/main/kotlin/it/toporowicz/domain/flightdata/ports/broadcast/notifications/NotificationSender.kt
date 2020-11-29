package it.toporowicz.domain.flightdata.ports.broadcast.notifications

interface NotificationSender {
    fun send(notification: Notification)
}