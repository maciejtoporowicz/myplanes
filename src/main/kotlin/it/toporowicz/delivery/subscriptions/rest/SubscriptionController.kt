package it.toporowicz.delivery.subscriptions.rest

import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import it.toporowicz.features.notifications.SubscriptionsModule
import it.toporowicz.features.notifications.core.subscriptions.SubscriptionData

data class JobId(val jobId: String)

@Controller("/subscriptions")
class SubscriptionController(private val subscriptionsModule: SubscriptionsModule) {
    @Get("/client/{clientId}")
    @Produces(MediaType.APPLICATION_JSON)
    fun getSubscriptionsData(clientId: String): Set<SubscriptionData> {
        return subscriptionsModule.getSubscriptionsData(clientId)
    }

    @Post("/client/{clientId}")
    @Produces(MediaType.APPLICATION_JSON)
    fun subscribeTo(clientId: String, @Body jobId: JobId) {
        return subscriptionsModule.subscribeToNotifications(jobId.jobId, clientId)
    }

    @Delete("/client/{clientId}")
    @Produces(MediaType.APPLICATION_JSON)
    fun unsubscribeFrom(clientId: String, @Body jobId: JobId) {
        return subscriptionsModule.unsubscribeFromNotifications(jobId.jobId, clientId)
    }
}