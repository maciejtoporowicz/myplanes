package it.toporowicz.cron.flightbroadcast

import io.micronaut.context.annotation.Prototype
import it.toporowicz.cron.flightbroadcast.config.FlightBroadcastJobConfigService
import it.toporowicz.domain.radar.core.RadarModule
import org.quartz.Job
import org.quartz.JobExecutionContext

@Prototype
class FlightBroadcastQuartzJob(private val radarModule: RadarModule, private val configService: FlightBroadcastJobConfigService) : Job {
    override fun execute(context: JobExecutionContext) {
        val config = configService.readFrom(context.jobDetail.jobDataMap)

        radarModule.cacheRadarDataAccordingTo(config)
    }
}