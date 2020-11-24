package it.toporowicz.delivery.flightdata

import io.micronaut.context.annotation.ConfigurationProperties
import io.micronaut.context.annotation.Factory
import it.toporowicz.features.flightdata.adapter.cache.RedisBasedFlightDataCache
import it.toporowicz.storage.FlightDataCache
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
    fun create(jedisPool: JedisPool, redisStorageConfig: RedisStorageConfig, objectMapper: ObjectMapper): FlightDataCache {
        return RedisBasedFlightDataCache(
                jedisPool,
                objectMapper,
                redisStorageConfig.expireAfterSeconds
        )
    }
}