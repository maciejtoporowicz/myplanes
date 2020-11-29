package it.toporowicz.application.subscriptions

import it.toporowicz.domain.jobs.core.JobsModule
import it.toporowicz.domain.subscriptions.api.JobData
import it.toporowicz.domain.subscriptions.core.SubscriptionModule
import javax.inject.Singleton

@Singleton
class SubscriptionsDataComposer(
        private val subscriptionModule: SubscriptionModule,
        private val jobsModule: JobsModule
) {
    fun getDataOfJobsToWhichClientIsSubscribed(clientId: String): Set<JobData> {
        val idsOfSubscriptions = subscriptionModule.getIdsOfJobsToWhichClientIsSubscribed(clientId)

        return jobsModule
                .findJobsByIds(idsOfSubscriptions)
                .map { job ->
                    JobData(
                            job.jobId,
                            job.name,
                            job.coordinates,
                            job.boundaryOffsetNorth,
                            job.boundaryOffsetEast,
                            job.boundaryOffsetSouth,
                            job.boundaryOffsetWest,
                            job.altitudeThreshold
                    )
                }
                .toSet()
    }
}