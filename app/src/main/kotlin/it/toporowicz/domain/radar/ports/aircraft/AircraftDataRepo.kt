package it.toporowicz.domain.radar.ports.aircraft

import it.toporowicz.domain.radar.api.AircraftData
import it.toporowicz.domain.radar.api.Icao24

interface AircraftDataRepo {
    fun getDataBy(icao24s: Set<Icao24>): Map<Icao24, AircraftData>
    fun putData(aircraftData: AircraftData)
}