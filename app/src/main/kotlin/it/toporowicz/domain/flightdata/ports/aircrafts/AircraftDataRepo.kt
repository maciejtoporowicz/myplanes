package it.toporowicz.domain.flightdata.ports.aircrafts

import it.toporowicz.domain.flightdata.api.AircraftData
import it.toporowicz.domain.flightdata.api.Icao24

interface AircraftDataRepo {
    fun getDataBy(icao24s: Set<Icao24>): Map<Icao24, AircraftData>
    fun putData(aircraftData: AircraftData)
}