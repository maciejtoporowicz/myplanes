package it.toporowicz.domain.subscriptions.port

interface SubscriptionRepo {
    fun save(subscription: Subscription)
    fun remove(subscription: Subscription)
    fun getIdsOfJobsToWhichClientIsSubscribed(clientId: String): Set<String>
}
