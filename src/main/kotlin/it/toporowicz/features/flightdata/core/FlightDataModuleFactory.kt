package it.toporowicz.features.flightdata.core

import it.toporowicz.features.flightdata.core.aircrafts.AircraftDataProvider
import it.toporowicz.features.flightdata.core.broadcast.FlightBroadcaster
import it.toporowicz.features.flightdata.core.broadcast.notifications.NotificationSender
import it.toporowicz.features.flightdata.core.coordinates.CoordinatesBoundaryCreator
import it.toporowicz.features.flightdata.core.radarData.RadarDataProvider
import it.toporowicz.features.flightdata.core.storage.FlightDataStorage
import it.toporowicz.features.notifications.core.subscriptions.SubscriptionManager

class FlightDataModuleFactory {
    fun create(radarDataProvider: RadarDataProvider, flightDataStorage: FlightDataStorage,
               notificationSender: NotificationSender, aircraftDataProvider: AircraftDataProvider): FlightDataModule {
        return FlightDataModule(
                CoordinatesBoundaryCreator(),
                radarDataProvider,
                FlightBroadcaster(
                        notificationSender
                ),
                flightDataStorage,
                aircraftDataProvider
        )
    }
}