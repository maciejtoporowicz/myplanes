package it.toporowicz.delivery.flightdata

import io.micronaut.context.annotation.Factory
import it.toporowicz.FlightDataModule
import it.toporowicz.FlightDataModuleFactory
import it.toporowicz.aircrafts.AircraftDataProvider
import it.toporowicz.storage.FlightDataCache
import it.toporowicz.broadcast.notifications.NotificationSender
import it.toporowicz.radarData.RadarDataProvider
import javax.inject.Singleton

@Factory
class FlightBroadcasterModuleFactory {
    @Singleton
    fun create(radarDataProvider: RadarDataProvider, flightDataCache: FlightDataCache, notificationSender: NotificationSender,
               aircraftDataProvider: AircraftDataProvider): FlightDataModule {
        return FlightDataModuleFactory().create(
                radarDataProvider,
                flightDataCache,
                notificationSender,
                aircraftDataProvider
        )
    }
}