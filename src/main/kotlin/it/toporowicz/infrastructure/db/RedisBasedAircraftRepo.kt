package it.toporowicz.infrastructure.db

import it.toporowicz.infrastructure.mapper.ObjectMapper
import redis.clients.jedis.JedisPool
import javax.inject.Singleton

typealias Icao24 = String

object RedisKey {
    fun forAircraft(icao24: Icao24) = "aircraft:${icao24}"
}

data class AircraftData (
        val icao24: String,
        val make: String?,
        val model: String?,
        val owner: String?
)

@Singleton
class RedisBasedAircraftRepo(private val jedisPool: JedisPool, private val objectMapper: ObjectMapper) {
    fun getDataBy(icao24s: Set<Icao24>): Map<Icao24, AircraftData> {
        return jedisPool.resource.use { jedis ->
            icao24s
                    .map { icao24: Icao24 -> jedis.get(RedisKey.forAircraft(icao24)) }
                    .filterNotNull()
                    .map { serializedAircraftData -> objectMapper.fromString<AircraftData>(serializedAircraftData) }
                    .associateBy { aircraftData -> aircraftData.icao24 }
        }
    }

    fun putData(aircraftData: AircraftData) {
        jedisPool.resource.use {
            jedis -> jedis.set(RedisKey.forAircraft(aircraftData.icao24), objectMapper.toJson(aircraftData))
        }
    }
}