package it.toporowicz.delivery.flightdata.quartz

import io.micronaut.context.annotation.Context
import it.toporowicz.delivery.flightdata.config.JobParamsProvider
import org.quartz.*
import org.quartz.impl.StdSchedulerFactory
import javax.annotation.PostConstruct

@Context
class JobStarter(private val micronautJobFactory: MicronautJobFactory, private val jobParamsProvider: JobParamsProvider, private val flightBroadcastJobConfigService: FlightBroadcastJobConfigService) {
    @PostConstruct
    fun startJobs() {
        try {
            val scheduler = StdSchedulerFactory.getDefaultScheduler()
            scheduler.setJobFactory(micronautJobFactory)
            scheduler.start()

            val flightBroadcasterJobsGroupName = "flightBroadcasters"
            val failOnDuplicateJobs = false

            val jobs = jobParamsProvider.read()
                    .map { jobParams ->
                        JobBuilder.newJob(FlightBroadcastJob::class.java)
                                .setJobData(flightBroadcastJobConfigService.createJobDataMapFrom(jobParams))
                                .withIdentity(jobParams.jobId, flightBroadcasterJobsGroupName)
                                .build()
                    }

            val jobsToTrigger = jobs
                    .map { job ->
                        job to setOf(triggerFor(job, flightBroadcasterJobsGroupName))
                    }.toMap()

            scheduler.scheduleJobs(jobsToTrigger, failOnDuplicateJobs)
        } catch (se: SchedulerException) {
            throw RuntimeException(se)
        }
    }

    private fun triggerFor(job: JobDetail, flightBroadcasterJobsGroupName: String): SimpleTrigger? {
        val flightBroadcasterJobsTriggerName = "flightBroadcastersTrigger"

        return TriggerBuilder.newTrigger()
                .withIdentity("$flightBroadcasterJobsTriggerName-${job.key.name}", flightBroadcasterJobsGroupName)
                .startNow()
                .withSchedule(
                        SimpleScheduleBuilder.simpleSchedule()
                                .withIntervalInSeconds(30)
                                .repeatForever()
                )
                .build()
    }
}