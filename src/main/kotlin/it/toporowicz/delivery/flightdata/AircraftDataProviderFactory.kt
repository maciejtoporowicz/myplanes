package it.toporowicz.delivery.flightdata

import io.micronaut.context.annotation.Factory
import it.toporowicz.features.flightdata.adapter.aircrafts.RedisBasedAircraftDataProvider
import it.toporowicz.features.flightdata.core.aircrafts.AircraftDataProvider
import it.toporowicz.infrastructure.db.RedisBasedAircraftRepo
import javax.inject.Singleton

@Factory
class AircraftDataProviderFactory {
    @Singleton
    fun create(redisBasedAircraftRepo: RedisBasedAircraftRepo): AircraftDataProvider {
        return RedisBasedAircraftDataProvider(
                redisBasedAircraftRepo
        )
    }
}