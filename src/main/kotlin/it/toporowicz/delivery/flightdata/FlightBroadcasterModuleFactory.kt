package it.toporowicz.delivery.flightdata

import io.micronaut.context.annotation.Factory
import it.toporowicz.features.flightdata.core.FlightDataModule
import it.toporowicz.features.flightdata.core.FlightDataModuleFactory
import it.toporowicz.features.flightdata.core.aircrafts.AircraftDataProvider
import it.toporowicz.features.flightdata.core.storage.FlightDataStorage
import it.toporowicz.features.flightdata.core.broadcast.notifications.NotificationSender
import it.toporowicz.features.flightdata.core.radarData.RadarDataProvider
import javax.inject.Singleton

@Factory
class FlightBroadcasterModuleFactory {
    @Singleton
    fun create(radarDataProvider: RadarDataProvider, icao24Storage: FlightDataStorage, notificationSender: NotificationSender,
               aircraftDataProvider: AircraftDataProvider): FlightDataModule {
        return FlightDataModuleFactory().create(
                radarDataProvider,
                icao24Storage,
                notificationSender,
                aircraftDataProvider
        )
    }
}