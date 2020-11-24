package it.toporowicz.broadcast.notifications

interface NotificationSender {
    fun send(notification: Notification)
}