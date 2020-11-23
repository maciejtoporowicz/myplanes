package it.toporowicz.features.flightdata.adapter.storage

import it.toporowicz.features.flightdata.core.LastKnownRadarData
import it.toporowicz.features.flightdata.core.radarData.RadarData
import it.toporowicz.features.flightdata.core.storage.FlightDataStorage
import it.toporowicz.features.flightdata.core.storage.Icao24OfNewFlights
import it.toporowicz.infrastructure.mapper.ObjectMapper
import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPool
import java.time.Instant

object RedisKey {
    fun forKnownFlightIds(jobId: String) = "jobs:${jobId}:knownFlightIds"
    fun forKnownFlight(jobId: String, icao24: String) = "jobs:${jobId}:knownFlights:${icao24}"
    fun forLastUpdate(jobId: String) = "jobs:${jobId}:lastUpdate"
}

class RedisBasedFlightDataStorage(private val jedisPool: JedisPool, private val objectMapper: ObjectMapper, private val expireAfterSeconds: Int) :
        FlightDataStorage {

    override fun set(jobId: String, radarData: Set<RadarData>): Icao24OfNewFlights {
        return jedisPool.resource.use { jedis ->
            val icao24OfFreshFlights = radarData.map { radarDataItem -> radarDataItem.icao24 }

            val icao24OfFlightsKnownUpToThisMoment = getIcao24OfKnownFlights(jobId, jedis)
            val icao24OfFlightsNoLongerPresent = icao24OfFlightsKnownUpToThisMoment - icao24OfFreshFlights
            val icao24OfNewFlights = icao24OfFreshFlights - icao24OfFlightsKnownUpToThisMoment

            val keyForKnownFlightIds = RedisKey.forKnownFlightIds(jobId)

            if (icao24OfFlightsNoLongerPresent.count() > 0) {
                jedis.srem(keyForKnownFlightIds, *(icao24OfFlightsNoLongerPresent.toTypedArray()))
                jedis.del(
                        *(icao24OfFlightsNoLongerPresent
                                .map { icao24 -> RedisKey.forKnownFlight(jobId, icao24) }
                                .toTypedArray())
                )
            }
            if(icao24OfFreshFlights.count() > 0) {
                jedis.sadd(keyForKnownFlightIds, *(icao24OfFreshFlights.toTypedArray()))
            }
            jedis.expire(keyForKnownFlightIds, expireAfterSeconds)
            radarData.forEach { radarDataItem ->
                jedis.setex(
                        RedisKey.forKnownFlight(jobId, radarDataItem.icao24),
                        expireAfterSeconds,
                        objectMapper.toJson(radarDataItem)
                )
            }
            jedis.setex(RedisKey.forLastUpdate(jobId), expireAfterSeconds, Instant.now().toString())

            icao24OfNewFlights.toSet()
        }
    }

    override fun get(jobId: String): LastKnownRadarData? {
        return jedisPool.resource.use {jedis ->
            val lastUpdate = jedis.get(RedisKey.forLastUpdate(jobId)) ?: return null

            val icao24OfKnownFlights = getIcao24OfKnownFlights(jobId, jedis)

            val updatedAt = Instant.parse(lastUpdate)
            val radarData = icao24OfKnownFlights
                    .map { jedis.get(RedisKey.forKnownFlight(jobId, it)) }
                    .filterNotNull()
                    .map { objectMapper.fromString<RadarData>(it) }
                    .toSet()

            LastKnownRadarData(jobId, updatedAt, radarData)
        }
    }

    private fun getIcao24OfKnownFlights(jobId: String, jedis: Jedis): Set<String> {
        return jedis.smembers(RedisKey.forKnownFlightIds(jobId))
    }
}