package it.toporowicz.features.notifications.core.subscriptions

interface SubscriptionManager {
    fun subscribeToNotifications(jobId: String, clientId: String)
    fun unsubscribeFromNotifications(jobId: String, clientId: String)
    fun getSubscriptionsData(clientId: String): Set<SubscriptionData>
}