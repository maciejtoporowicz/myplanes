package it.toporowicz.delivery.flightdata

import io.micronaut.context.annotation.ConfigurationProperties
import io.micronaut.context.annotation.Factory
import it.toporowicz.features.flightdata.adapter.storage.RedisBasedFlightDataStorage
import it.toporowicz.features.flightdata.core.storage.FlightDataStorage
import it.toporowicz.infrastructure.mapper.ObjectMapper
import redis.clients.jedis.JedisPool
import javax.inject.Singleton

@ConfigurationProperties("storage.redis")
interface RedisStorageConfig {
    val expireAfterSeconds: Int
}

@Factory
internal class BroadcastedIcao24CacheFactory {
    @Singleton
    fun create(jedisPool: JedisPool, redisStorageConfig: RedisStorageConfig, objectMapper: ObjectMapper): FlightDataStorage {
        return RedisBasedFlightDataStorage(
                jedisPool,
                objectMapper,
                redisStorageConfig.expireAfterSeconds
        )
    }
}