package integration.flightdata

import integration.flightdata.fakes.FakeApplicationEventPublisher
import integration.flightdata.mocks.OpenSkyApiMock
import io.cucumber.datatable.DataTable
import io.cucumber.java8.En
import it.toporowicz.domain.radar.api.AircraftData
import it.toporowicz.domain.radar.RadarModuleFactory
import it.toporowicz.domain.radar.OpenSkyApiConfig
import it.toporowicz.domain.radar.api.*
import it.toporowicz.domain.radar.core.RadarModule
import it.toporowicz.domain.radar.ports.broadcast.notifications.NewFlightsEvent
import it.toporowicz.domain.radar.ports.radarData.RadarData
import it.toporowicz.infrastructure.mapper.ObjectMapperFactory
import org.assertj.core.api.Assertions.assertThat
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.testcontainers.containers.GenericContainer
import org.testcontainers.utility.DockerImageName
import redis.clients.jedis.JedisPool
import java.math.BigDecimal
import java.time.Clock
import java.time.Instant

const val OPEN_SKY_API_USER = "username123"
const val OPEN_SKY_API_PASS = "password123"

class FlightDataStep : En {
    private val clock = mock(Clock::class.java)
    private val redis: GenericContainer<Nothing> = GenericContainer<Nothing>(DockerImageName.parse("redis:5.0.3-alpine"))
            .withExposedPorts(6379)
    private val jedisPool: JedisPool
    private val openSkyApiMock = OpenSkyApiMock(OPEN_SKY_API_USER, OPEN_SKY_API_PASS)

    private val fakeApplicationEventPublisher = FakeApplicationEventPublisher()

    private val module: RadarModule

    init {
        redis.start()

        jedisPool = JedisPool("localhost", this.redis.firstMappedPort)

        val openSkyApiConfig = object : OpenSkyApiConfig {
            override val url: String
                get() = "http://localhost:${openSkyApiMock.port()}"
            override val user: String
                get() = OPEN_SKY_API_USER
            override val password: String
                get() = OPEN_SKY_API_PASS
        }

        this.module = RadarModuleFactory().create(
                clock,
                jedisPool,
                ObjectMapperFactory().create(),
                openSkyApiConfig,
                fakeApplicationEventPublisher
        )

        configureSteps()
    }

    private fun configureSteps() {
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
                this.module.addAircraftData(it)
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

            val flightScannerConfig = RadarConfig(
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

            this.module.cacheRadarDataAccordingTo(flightScannerConfig)
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
                        rowMap["onGround"]?.let { it.toBoolean() },
                        rowMap["longitude"]?.let { DecimalDegrees(BigDecimal(it)) },
                        rowMap["latitude"]?.let { DecimalDegrees(BigDecimal(it)) },
                        rowMap["track"]?.let { Track(BigDecimal(it)) }
                )
            }.toSet()

            this.openSkyApiMock.givenRadarData(radarData, latMin, latMax, lonMin, lonMax)
        }

        Then("the following 'New flights event' should be sent") { notificationsTable: DataTable ->
            val notification = notificationsTable.asMaps()[0].let { rowMap ->
                aNewFlightsEvent(
                        requireNotNull(rowMap["jobId"]),
                        Integer.parseInt(requireNotNull(rowMap["newFlightsCount"]))
                )
            }

            assertThat(fakeApplicationEventPublisher.getLastSentEvent()).isEqualTo(notification)
        }

        Then("no notifications should be sent") {
            assertThat(fakeApplicationEventPublisher.getLastSentEvent()).isNull()
        }

        Then("querying data for job with id={string} provides the following results updated at {int}") { jobId: String, time: Int, expectedFlightDataTable: DataTable ->
            val expectedFlightData = expectedFlightDataTable.asMaps().map { rowMap ->
                FlightData(
                        requireNotNull(rowMap["icao24"]),
                        rowMap["callSign"],
                        rowMap["barometricAltitude"]?.let { Distance.meters(BigDecimal(it)) },
                        rowMap["onGround"]?.let { it.toBoolean() },
                        rowMap["longitude"]?.let { DecimalDegrees(BigDecimal(it)) },
                        rowMap["latitude"]?.let { DecimalDegrees(BigDecimal(it)) },
                        rowMap["make"],
                        rowMap["model"],
                        rowMap["owner"]
                )
            }
            val expectedData = LastKnownFlightData(
                    jobId, Instant.ofEpochSecond(time.toLong()),
                    expectedFlightData
            )

            val actualData = module.queries().getLastKnownFlightDataFor(jobId)

            assertThat(actualData).isEqualTo(expectedData)
        }

        Then("querying data for job with id={string} provides no results") { jobId: String ->

            val actualData = module.queries().getLastKnownFlightDataFor(jobId)

            assertThat(actualData).isNull()
        }

        Then("querying data for job with id={string} provides empty results at time {int}") { jobId: String, time: Int ->

            val actualData = module.queries().getLastKnownFlightDataFor(jobId)

            assertThat(actualData).isEqualTo(LastKnownFlightData(jobId, Instant.ofEpochSecond(time.toLong()), emptyList()))
        }
    }

    private fun mockTime(time: Int) {
        Mockito.reset(clock)
        given(clock.instant()).willReturn(Instant.ofEpochSecond(time.toLong()))
    }

    private fun aNewFlightsEvent(jobId: String, newFlightsCount: Int): NewFlightsEvent {
        return NewFlightsEvent(jobId, newFlightsCount)
    }
}