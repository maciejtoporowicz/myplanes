package it.toporowicz.features.flightdata.core.aircrafts

import it.toporowicz.features.flightdata.core.Icao24

interface AircraftDataProvider {
    fun getDataBy(icao24s: Set<Icao24>): Map<Icao24, AircraftData>
}