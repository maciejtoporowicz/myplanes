package it.toporowicz.features.notifications

import it.toporowicz.features.notifications.core.subscriptions.SubscriptionData
import it.toporowicz.features.notifications.core.subscriptions.SubscriptionManager

class SubscriptionsModule(
        private val subscriptionManager: SubscriptionManager,
) {
    fun subscribeToNotifications(jobId: String, clientId: String) {
        subscriptionManager.subscribeToNotifications(jobId, clientId)
    }

    fun unsubscribeFromNotifications(jobId: String, clientId: String) {
        subscriptionManager.unsubscribeFromNotifications(jobId, clientId)
    }

    fun getSubscriptionsData(clientId: String): Set<SubscriptionData> {
        return subscriptionManager.getSubscriptionsData(clientId)
    }
}