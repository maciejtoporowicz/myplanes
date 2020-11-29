package integration.flightdata

import integration.flightdata.fakes.FakePushMessagingService
import integration.flightdata.mocks.OpenSkyApiMock
import io.cucumber.datatable.DataTable
import io.cucumber.java8.En
import it.toporowicz.domain.flightdata.api.*
import it.toporowicz.domain.flightdata.core.FlightDataModule
import it.toporowicz.domain.flightdata.FlightDataModuleFactory
import it.toporowicz.domain.flightdata.OpenSkyApiConfig
import it.toporowicz.domain.flightdata.ports.radarData.RadarData
import it.toporowicz.infrastructure.mapper.ObjectMapperFactory
import it.toporowicz.infrastructure.pushmessaging.Message
import org.assertj.core.api.Assertions.assertThat
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import org.mockito.Mockito.mock
import redis.clients.jedis.JedisPool
import redis.embedded.RedisServer
import java.math.BigDecimal
import java.time.Clock
import java.time.Instant

const val REDIS_PORT = 49555
const val OPEN_SKY_API_USER = "username123"
const val OPEN_SKY_API_PASS = "password123"

class FlightDataStep : En {
    private val clock = mock(Clock::class.java)
    private val redis = RedisServer.builder().port(REDIS_PORT).setting("maxheap 128M").build()
    private val jedisPool = JedisPool("localhost", REDIS_PORT)
    private val openSkyApiMock = OpenSkyApiMock(OPEN_SKY_API_USER, OPEN_SKY_API_PASS)

    private val pushNotificationService = FakePushMessagingService()

    private val module: FlightDataModule

    init {

        val openSkyApiConfig = object : OpenSkyApiConfig {
            override val url: String
                get() = "http://localhost:${openSkyApiMock.port()}"
            override val user: String
                get() = OPEN_SKY_API_USER
            override val password: String
                get() = OPEN_SKY_API_PASS
        }

        this.module = FlightDataModuleFactory().create(
                clock,
                pushNotificationService,
                jedisPool,
                ObjectMapperFactory().create(),
                openSkyApiConfig
        )

        configureSteps()
    }

    private fun configureSteps() {
        Before { _ ->
            redis.start()
        }

        After { _ ->
            openSkyApiMock.stop()
            redis.stop()
        }

        Given("aircraft data storage contains the following data")
        { aircraftDataTable: DataTable ->
            aircraftDataTable.asMaps().map { rowMap ->
                AircraftData(
                        requireNotNull(rowMap["icao24"]),
                        rowMap["make"],
                        rowMap["model"],
                        rowMap["owner"],
                )
            }.forEach {
                this.module.putAircraftData(it)
            }
        }

        Given("current time is {int}") { time: Int ->
            mockTime(time)
        }

        When("time is advanced to {int}") { time: Int ->
            mockTime(time)
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

        Given("radar provides no flights for latMax={string}, lonMax={string}, latMin={string}, lonMin={string}")
        { latMax: String, lonMax: String, latMin: String, lonMin: String ->
            openSkyApiMock.givenRadarData(emptySet(), latMin, latMax, lonMin, lonMax)
        }

        Given("radar provides the following flights for latMax={string}, lonMax={string}, latMin={string}, lonMin={string}")
        { latMax: String, lonMax: String, latMin: String, lonMin: String, radarDataTable: DataTable ->
            val radarData = radarDataTable.asMaps().map { rowMap ->
                RadarData(
                        requireNotNull(rowMap["icao24"]),
                        rowMap["callSign"],
                        rowMap["barometricAltitude"]?.let { Distance(BigDecimal(it)) },
                        rowMap["onGround"]?.let { it.toBoolean() }
                )
            }.toSet()

            this.openSkyApiMock.givenRadarData(radarData, latMin, latMax, lonMin, lonMax)
        }

        Then("the following notification should be sent") { notificationsTable: DataTable ->
            val notification = notificationsTable.asMaps()[0].let { rowMap ->
                aNotificationMessage(
                        requireNotNull(rowMap["jobId"]),
                        Integer.parseInt(requireNotNull(rowMap["newFlightsCount"]))
                )
            }

            assertThat(pushNotificationService.getLastSentNotification()).isEqualTo(notification.data["jobId"] to notification)
        }

        Then("no notifications should be sent") {
            assertThat(pushNotificationService.getLastSentNotification()).isNull()
        }

        Then("querying data for job with id={string} provides the following results updated at {int}") { jobId: String, time: Int, expectedFlightDataTable: DataTable ->
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
            }
            val expectedData = LastKnownFlightsData(
                    jobId, Instant.ofEpochSecond(time.toLong()),
                    expectedFlightData
            )

            val actualData = module.getLastKnownFlightDataFor(jobId)

            assertThat(actualData).isEqualTo(expectedData)
        }

        Then("querying data for job with id={string} provides no results") { jobId: String ->

            val actualData = module.getLastKnownFlightDataFor(jobId)

            assertThat(actualData).isNull()
        }

        Then("querying data for job with id={string} provides empty results at time {int}") { jobId: String, time: Int ->

            val actualData = module.getLastKnownFlightDataFor(jobId)

            assertThat(actualData).isEqualTo(LastKnownFlightsData(jobId, Instant.ofEpochSecond(time.toLong()), emptyList()))
        }
    }

    private fun mockTime(time: Int) {
        Mockito.reset(clock)
        given(clock.instant()).willReturn(Instant.ofEpochSecond(time.toLong()))
    }

    private fun aNotificationMessage(jobId: String, newFlightsCount: Int): Message {
        return Message(
                mapOf(
                        "jobId" to jobId,
                        "newFlightsCount" to newFlightsCount.toString()
                )
        )
    }
}