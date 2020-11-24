package it.toporowicz.delivery.flightdata.quartz

import io.micronaut.context.annotation.Prototype
import it.toporowicz.FlightDataModule
import org.quartz.Job
import org.quartz.JobExecutionContext

@Prototype
class FlightBroadcastJob(private val flightDataModule: FlightDataModule, private val configService: FlightBroadcastJobConfigService) : Job {
    override fun execute(context: JobExecutionContext) {
        val config = configService.readFrom(context.jobDetail.jobDataMap)

        flightDataModule.broadcastDataAccordingTo(config)
    }
}