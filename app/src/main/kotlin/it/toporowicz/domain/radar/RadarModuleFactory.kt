package it.toporowicz.domain.radar

import io.micronaut.context.annotation.ConfigurationProperties
import io.micronaut.context.annotation.Factory
import io.micronaut.context.event.ApplicationEventPublisher
import it.toporowicz.domain.radar.adapters.aircrafts.RedisBasedAircraftDataRepo
import it.toporowicz.domain.radar.adapters.broadcast.notifications.PushMessageNewFlightEventSender
import it.toporowicz.domain.radar.adapters.cache.RedisBasedFlightDataCache
import it.toporowicz.domain.radar.adapters.radarData.ApiConfig
import it.toporowicz.domain.radar.adapters.radarData.OpenSkyApiRadarDataProvider
import it.toporowicz.domain.radar.core.RadarModule
import it.toporowicz.domain.radar.core.RadarQueries
import it.toporowicz.domain.radar.core.broadcast.EventBroadcaster
import it.toporowicz.domain.radar.core.coordinates.CoordinatesBoundaryCreator
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
class RadarModuleFactory {
    @Singleton
    fun create(
            clock: Clock,
            jedisPool: JedisPool,
            objectMapper: ObjectMapper,
            openSkyApiConfig: OpenSkyApiConfig,
            applicationEventPublisher: ApplicationEventPublisher
    ): RadarModule {
        val radarDataProvider = OpenSkyApiRadarDataProvider(
                ApiConfig(openSkyApiConfig.url, openSkyApiConfig.user, openSkyApiConfig.password),
                objectMapper
        )
        val flightDataCache = RedisBasedFlightDataCache(jedisPool, objectMapper, clock)
        val notificationSender = PushMessageNewFlightEventSender(applicationEventPublisher)
        val aircraftDataRepo = RedisBasedAircraftDataRepo(jedisPool, objectMapper)

        val queries = RadarQueries(flightDataCache, aircraftDataRepo)

        return RadarModule(
                CoordinatesBoundaryCreator(),
                radarDataProvider,
                EventBroadcaster(
                        notificationSender
                ),
                flightDataCache,
                aircraftDataRepo,
                queries
        )
    }
}