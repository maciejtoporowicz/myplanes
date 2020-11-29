package it.toporowicz.domain.flightdata.adapters.aircrafts

import it.toporowicz.domain.flightdata.api.AircraftData
import it.toporowicz.domain.flightdata.api.Icao24
import it.toporowicz.domain.flightdata.ports.aircrafts.AircraftDataRepo
import it.toporowicz.infrastructure.mapper.ObjectMapper
import redis.clients.jedis.JedisPool

object RedisKey {
    fun forAircraft(icao24: Icao24) = "aircraft:${icao24}"
}

class RedisBasedAircraftDataRepo(private val jedisPool: JedisPool, private val objectMapper: ObjectMapper) : AircraftDataRepo {
    override fun getDataBy(icao24s: Set<Icao24>): Map<Icao24, AircraftData> {
        return jedisPool.resource.use { jedis ->
            icao24s
                    .map { icao24: Icao24 -> jedis.get(RedisKey.forAircraft(icao24)) }
                    .filterNotNull()
                    .map { serializedAircraftData -> objectMapper.fromString<AircraftData>(serializedAircraftData) }
                    .associateBy { aircraftData -> aircraftData.icao24 }
        }
    }

    override fun putData(aircraftData: AircraftData) {
        jedisPool.resource.use {
            jedis -> jedis.set(RedisKey.forAircraft(aircraftData.icao24), objectMapper.toJson(aircraftData))
        }
    }
}