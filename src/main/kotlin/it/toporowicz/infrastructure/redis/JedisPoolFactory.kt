package it.toporowicz.infrastructure.redis

import io.micronaut.context.annotation.ConfigurationProperties
import io.micronaut.context.annotation.Factory
import redis.clients.jedis.JedisPool
import javax.inject.Singleton

@ConfigurationProperties("redis")
interface RedisConfig {
    val host: String
    val port: Int
}

@Factory
internal class JedisPoolFactory {
    @Singleton
    fun create(config: RedisConfig): JedisPool {
        return JedisPool(config.host, config.port)
    }
}