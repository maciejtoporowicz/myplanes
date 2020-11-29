package it.toporowicz.domain.subscriptions.core

import it.toporowicz.domain.subscriptions.port.Subscription
import it.toporowicz.domain.subscriptions.port.SubscriptionRepo

class SubscriptionModule(private val subscriptionRepo: SubscriptionRepo) {
    fun subscribeToNotifications(jobId: String, clientId: String) {
        subscriptionRepo.save(Subscription(jobId, clientId))
    }

    fun unsubscribeFromNotifications(jobId: String, clientId: String) {
        subscriptionRepo.remove(Subscription(jobId, clientId))
    }

    fun getIdsOfJobsToWhichClientIsSubscribed(clientId: String): Set<String> {
        return subscriptionRepo.getIdsOfJobsToWhichClientIsSubscribed(clientId)
    }
}