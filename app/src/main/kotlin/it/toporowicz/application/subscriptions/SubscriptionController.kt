package it.toporowicz.application.subscriptions

import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import it.toporowicz.domain.subscriptions.api.JobData
import it.toporowicz.domain.subscriptions.core.SubscriptionModule

data class JobId(val jobId: String)

@Controller("/subscriptions")
class SubscriptionController(private val subscriptionsModule: SubscriptionModule, private val subscriptionsDataComposer: SubscriptionsDataComposer) {
    @Get("/client/{clientId}")
    @Produces(MediaType.APPLICATION_JSON)
    fun getSubscriptionsData(clientId: String): Set<JobData> {
        return subscriptionsDataComposer.getDataOfJobsToWhichClientIsSubscribed(clientId)
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