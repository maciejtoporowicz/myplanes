package it.toporowicz.cron.flightbroadcast

import io.micronaut.context.annotation.Prototype
import it.toporowicz.cron.flightbroadcast.config.FlightBroadcastJobConfigService
import it.toporowicz.domain.flightdata.core.FlightDataModule
import org.quartz.Job
import org.quartz.JobExecutionContext

@Prototype
class FlightBroadcastQuartzJob(private val flightDataModule: FlightDataModule, private val configService: FlightBroadcastJobConfigService) : Job {
    override fun execute(context: JobExecutionContext) {
        val config = configService.readFrom(context.jobDetail.jobDataMap)

        flightDataModule.broadcastDataAccordingTo(config)
    }
}