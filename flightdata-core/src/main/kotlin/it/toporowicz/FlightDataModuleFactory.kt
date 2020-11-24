package it.toporowicz

import it.toporowicz.aircrafts.AircraftDataProvider
import it.toporowicz.broadcast.FlightBroadcaster
import it.toporowicz.broadcast.notifications.NotificationSender
import it.toporowicz.coordinates.CoordinatesBoundaryCreator
import it.toporowicz.radarData.RadarDataProvider
import it.toporowicz.storage.FlightDataCache

class FlightDataModuleFactory {
    fun create(radarDataProvider: RadarDataProvider, flightDataCache: FlightDataCache,
               notificationSender: NotificationSender, aircraftDataProvider: AircraftDataProvider): FlightDataModule {
        return FlightDataModule(
                CoordinatesBoundaryCreator(),
                radarDataProvider,
                FlightBroadcaster(
                        notificationSender
                ),
                flightDataCache,
                aircraftDataProvider
        )
    }
}