package it.toporowicz.features.flightdata.adapter.aircrafts

import it.toporowicz.features.flightdata.core.Icao24
import it.toporowicz.features.flightdata.core.aircrafts.AircraftData
import it.toporowicz.features.flightdata.core.aircrafts.AircraftDataProvider
import it.toporowicz.infrastructure.db.RedisBasedAircraftRepo

class RedisBasedAircraftDataProvider(private val redisBasedAircraftRepo: RedisBasedAircraftRepo) : AircraftDataProvider {
    override fun getDataBy(icao24s: Set<Icao24>): Map<Icao24, AircraftData> {
        return redisBasedAircraftRepo
                .getDataBy(icao24s)
                .map { (icao24, aircraftData) -> icao24 to AircraftData(aircraftData.icao24, aircraftData.make, aircraftData.model, aircraftData.owner) }
                .toMap()
    }
}