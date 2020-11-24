package cucumber.features.flightdata

import cucumber.features.flightdata.mocks.FakeAircraftDataProvider
import cucumber.features.flightdata.mocks.FakeFlightDataCache
import io.cucumber.datatable.DataTable
import io.cucumber.java8.En
import it.toporowicz.*
import it.toporowicz.aircrafts.AircraftData
import it.toporowicz.broadcast.notifications.Notification
import it.toporowicz.broadcast.notifications.NotificationSender
import it.toporowicz.coordinates.Coordinates
import it.toporowicz.coordinates.CoordinatesBoundary
import it.toporowicz.coordinates.DecimalDegrees
import it.toporowicz.coordinates.Distance
import it.toporowicz.radarData.RadarData
import it.toporowicz.radarData.RadarDataProvider
import org.assertj.core.api.Assertions.assertThat
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.then
import org.mockito.Mockito
import org.mockito.Mockito.mock
import java.math.BigDecimal
import java.time.Clock
import java.time.Instant

class FlightDataStep : En {
    private val clock = mock(Clock::class.java)

    private val module: FlightDataModule

    private val radarDataProvider = mock(RadarDataProvider::class.java)
    private val notificationSender = mock(NotificationSender::class.java)
    private val flightDataStorage = FakeFlightDataCache(clock)
    private val aircraftDataProvider = FakeAircraftDataProvider()

    init {
        println("new instance")
        this.module = FlightDataModuleFactory().create(
                radarDataProvider,
                flightDataStorage,
                notificationSender,
                aircraftDataProvider
        )

        configureSteps()
    }

    private fun configureSteps() {
        Given("aircraft data storage contains the following data")
        {
            aircraftDataTable: DataTable ->
            val aircraftDataItems = aircraftDataTable.asMaps().map {
                rowMap -> AircraftData(
                    requireNotNull(rowMap["icao24"]),
                    rowMap["make"],
                    rowMap["model"],
                    rowMap["owner"],
                )
            }.toSet()

            this.aircraftDataProvider.set(aircraftDataItems)
        }

        Given("current time is {int}") {
            time: Int -> mockTime(time)
        }

        When("time is advanced to {int}") {
            time: Int -> mockTime(time)
        }

        When("scanner is run with the following configuration")
        { jobConfigTable: DataTable ->
            val row = jobConfigTable.asMaps()[0]

            val flightScannerConfig = FlightScannerConfig(
                    requireNotNull(row["jobId"]),
                    Coordinates(
                            DecimalDegrees(BigDecimal(requireNotNull(row["lat"]))),
                            DecimalDegrees(BigDecimal(requireNotNull(row["lon"])))
                    ),
                    Distance(BigDecimal(requireNotNull(row["boundaryN"]))),
                    Distance(BigDecimal(requireNotNull(row["boundaryE"]))),
                    Distance(BigDecimal(requireNotNull(row["boundaryS"]))),
                    Distance(BigDecimal(requireNotNull(row["boundaryW"]))),
                    Distance(BigDecimal(requireNotNull(row["altitudeThreshold"])))
            )

            this.module.broadcastDataAccordingTo(flightScannerConfig)
        }

        Given("radar provides the following flights for latMax={string}, lonMax={string}, latMin={string}, lonMin={string}, maxAltitude={string}")
        { latMax: String, lonMax: String, latMin: String, lonMin: String, maxAlt: String, radarDataTable: DataTable ->
            val radarData = radarDataTable.asMaps().map { rowMap ->
                RadarData(
                        requireNotNull(rowMap["icao24"]),
                        rowMap["callSign"],
                        rowMap["barometricAltitude"]?.let { Distance(BigDecimal(it)) },
                        rowMap["onGround"]?.let { it.toBoolean() }
                )
            }.toSet()

            Mockito.reset(this.radarDataProvider)
            given(this.radarDataProvider.getRadarDataWithin(
                    CoordinatesBoundary(
                            DecimalDegrees(BigDecimal(latMax)),
                            DecimalDegrees(BigDecimal(lonMax)),
                            DecimalDegrees(BigDecimal(latMin)),
                            DecimalDegrees(BigDecimal(lonMin))
                    ), Distance(BigDecimal(maxAlt))
            )).willReturn(radarData)
        }

        Then("the following notification should be sent") {
            notificationsTable: DataTable ->
            val notification = notificationsTable.asMaps()[0].let { rowMap ->
                Notification(
                        requireNotNull(rowMap["jobId"]),
                        Integer.parseInt(requireNotNull(rowMap["newFlightsCount"]))
                )
            }

            then(notificationSender).should().send(notification)
        }

        Then("querying data for job with id={string} provides the following results updated at {int}") {
            jobId: String, time: Int, expectedFlightDataTable: DataTable ->
            val expectedFlightData = expectedFlightDataTable.asMaps().map { rowMap ->
                FlightData(
                        requireNotNull(rowMap["icao24"]),
                        rowMap["callSign"],
                        rowMap["barometricAltitude"]?.let { Distance.meters(BigDecimal(it)) },
                        rowMap["onGround"]?.let { it.toBoolean() },
                        rowMap["make"],
                        rowMap["model"],
                        rowMap["owner"]
                )
            }.toSet()
            val expectedData = LastKnownFlightsData(
                    jobId, Instant.ofEpochSecond(time.toLong()),
                    expectedFlightData
            )

            val actualData = module.getLastKnownFlightDataFor(jobId)

            assertThat(actualData).isEqualTo(expectedData)
        }

        Then("querying data for job with id={string} provides no results") {
            jobId: String ->

            val actualData = module.getLastKnownFlightDataFor(jobId)

            assertThat(actualData).isNull()
        }

        Then("querying data for job with id={string} provides empty results at time {int}") {
            jobId: String, time: Int ->

            val actualData = module.getLastKnownFlightDataFor(jobId)

            assertThat(actualData).isEqualTo(LastKnownFlightsData(jobId, Instant.ofEpochSecond(time.toLong()), emptySet()))
        }
    }

    private fun mockTime(time: Int) {
        Mockito.reset(clock)
        given(clock.instant()).willReturn(Instant.ofEpochSecond(time.toLong()))
    }
}