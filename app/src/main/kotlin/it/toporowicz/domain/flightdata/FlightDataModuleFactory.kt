package it.toporowicz.domain.flightdata

import io.micronaut.context.annotation.ConfigurationProperties
import io.micronaut.context.annotation.Factory
import it.toporowicz.domain.flightdata.adapters.aircrafts.RedisBasedAircraftDataRepo
import it.toporowicz.domain.flightdata.adapters.broadcast.notifications.PushMessageNotificationSender
import it.toporowicz.domain.flightdata.adapters.cache.RedisBasedFlightDataCache
import it.toporowicz.domain.flightdata.adapters.radarData.ApiConfig
import it.toporowicz.domain.flightdata.adapters.radarData.OpenSkyApiRadarDataProvider
import it.toporowicz.domain.flightdata.core.FlightDataModule
import it.toporowicz.domain.flightdata.core.broadcast.FlightBroadcaster
import it.toporowicz.domain.flightdata.core.coordinates.CoordinatesBoundaryCreator
import it.toporowicz.infrastructure.pushmessaging.PushMessagingService
import it.toporowicz.infrastructure.mapper.ObjectMapper
import redis.clients.jedis.JedisPool
import java.time.Clock
import javax.inject.Singleton
import javax.validation.constraints.NotNull

@ConfigurationProperties("openskyapi")
interface OpenSkyApiConfig {
    @get:NotNull
    val url: String

    @get:NotNull
    val user: String

    @get:NotNull
    val password: String
}

@Factory
class FlightDataModuleFactory {
    @Singleton
    fun create(
            clock: Clock,
            pushMessagingService: PushMessagingService,
            jedisPool: JedisPool,
            objectMapper: ObjectMapper,
            openSkyApiConfig: OpenSkyApiConfig
    ): FlightDataModule {
        val radarDataProvider = OpenSkyApiRadarDataProvider(
                ApiConfig(openSkyApiConfig.url, openSkyApiConfig.user, openSkyApiConfig.password),
                objectMapper
        )
        val flightDataCache = RedisBasedFlightDataCache(jedisPool, objectMapper, clock)
        val notificationSender = PushMessageNotificationSender(pushMessagingService)
        val aircraftDataRepo = RedisBasedAircraftDataRepo(jedisPool, objectMapper)

        return FlightDataModule(
                CoordinatesBoundaryCreator(),
                radarDataProvider,
                FlightBroadcaster(
                        notificationSender
                ),
                flightDataCache,
                aircraftDataRepo
        )
    }
}