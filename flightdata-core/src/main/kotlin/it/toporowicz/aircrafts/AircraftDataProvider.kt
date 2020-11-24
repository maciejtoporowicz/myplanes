package it.toporowicz.aircrafts

import it.toporowicz.Icao24

interface AircraftDataProvider {
    fun getDataBy(icao24s: Set<Icao24>): Map<Icao24, AircraftData>
}