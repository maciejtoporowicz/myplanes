package it.toporowicz.cron.flightbroadcast.config

import it.toporowicz.domain.flightdata.api.FlightScannerConfig
import it.toporowicz.domain.jobs.api.Job
import org.quartz.JobDataMap
import javax.inject.Singleton

@Singleton
class FlightBroadcastJobConfigService(private val coordinatesReader: CoordinatesReader, private val distanceReader: DistanceReader) {
    fun readFrom(jobDataMap: JobDataMap): FlightScannerConfig {
        return FlightScannerConfig(
                jobId = jobDataMap.getString("jobId"),
                coordinates = coordinatesReader.from(jobDataMap.getString("coordinates")),
                boundaryOffsetNorth = distanceReader.from(jobDataMap.getString("boundaryOffsetNorth")),
                boundaryOffsetEast = distanceReader.from(jobDataMap.getString("boundaryOffsetEast")),
                boundaryOffsetSouth = distanceReader.from(jobDataMap.getString("boundaryOffsetSouth")),
                boundaryOffsetWest = distanceReader.from(jobDataMap.getString("boundaryOffsetWest")),
                altitudeThreshold = distanceReader.from(jobDataMap.getString("altitudeThreshold"))
        )
    }

    fun createJobDataMapFrom(jobParams: Job): JobDataMap {
        return JobDataMap(
                mapOf(
                        "jobId" to jobParams.jobId,
                        "coordinates" to jobParams.coordinates,
                        "boundaryOffsetNorth" to jobParams.boundaryOffsetNorth,
                        "boundaryOffsetEast" to jobParams.boundaryOffsetEast,
                        "boundaryOffsetSouth" to jobParams.boundaryOffsetSouth,
                        "boundaryOffsetWest" to jobParams.boundaryOffsetWest,
                        "altitudeThreshold" to jobParams.altitudeThreshold
                )
        )
    }
}