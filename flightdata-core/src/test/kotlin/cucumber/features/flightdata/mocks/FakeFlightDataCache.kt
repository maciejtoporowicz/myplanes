package cucumber.features.flightdata.mocks

import it.toporowicz.LastKnownRadarData
import it.toporowicz.radarData.RadarData
import it.toporowicz.storage.FlightDataCache
import java.time.Clock
import java.time.Instant
import java.util.*

class FakeFlightDataCache(private val clock: Clock) : FlightDataCache {
    private val flightDataByJobId = HashMap<String, Set<RadarData>>()
    private val lastUpdateByJobId = HashMap<String, Instant>()

    override fun set(jobId: String, radarData: Set<RadarData>) {
        this.flightDataByJobId[jobId] = radarData
        this.lastUpdateByJobId[jobId] = this.clock.instant()
    }

    override fun get(jobId: String): LastKnownRadarData? {
        return this.flightDataByJobId[jobId]
                ?.let { radarDataItems ->
                    LastKnownRadarData(
                            jobId,
                            this.lastUpdateByJobId[jobId]
                                    ?: throw RuntimeException("Last update not found for job with id=$jobId"),
                            radarDataItems
                    )
                }
    }
}