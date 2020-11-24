package cucumber.features.flightdata.mocks

import it.toporowicz.LastKnownRadarData
import it.toporowicz.radarData.RadarData
import it.toporowicz.storage.FlightDataStorage
import it.toporowicz.storage.Icao24OfNewFlights
import java.time.Clock
import java.time.Instant
import java.util.*

class FakeFlightDataStorage(private val clock: Clock) : FlightDataStorage {
    private val flightDataByJobId = HashMap<String, Set<RadarData>>()
    private val lastUpdateByJobId = HashMap<String, Instant>()

    override fun set(jobId: String, radarData: Set<RadarData>): Icao24OfNewFlights {
        val icao24OfCurrentFlights = this.flightDataByJobId.values
                .flatMap { currentRadarData -> currentRadarData.map { dataItem -> dataItem.icao24 } }
                .toSet()
        val icao24OfFlightsToSet = radarData
                .map { it.icao24 }
                .toSet()

        val icao24OfNewFlights = icao24OfFlightsToSet - icao24OfCurrentFlights

        this.flightDataByJobId.merge(jobId, radarData) { oldVal, newVal ->
            oldVal.filter { !icao24OfFlightsToSet.contains(it.icao24) }.toSet().plus(newVal)
        }
        this.lastUpdateByJobId[jobId] = this.clock.instant()

        return icao24OfNewFlights
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

    fun overwrite(jobId: String, radarData: Set<RadarData>) {
        this.flightDataByJobId[jobId] = radarData
        this.lastUpdateByJobId[jobId] = this.clock.instant()
    }

    fun clearDataFor(jobId: String) {
        this.flightDataByJobId.remove(jobId)
        this.lastUpdateByJobId.remove(jobId)
    }
}