package cucumber.features.flightdata.mocks

import it.toporowicz.Icao24
import it.toporowicz.aircrafts.AircraftData
import it.toporowicz.aircrafts.AircraftDataProvider

class FakeAircraftDataProvider : AircraftDataProvider {
    private var aircraftDataItems: Map<Icao24, AircraftData> = emptyMap()

    override fun getDataBy(icao24s: Set<Icao24>): Map<Icao24, AircraftData> {
        return this.aircraftDataItems.filterKeys { icao24s.contains(it) }
    }

    fun set(aircraftData: Set<AircraftData>) {
        this.aircraftDataItems = aircraftData.associateBy { it.icao24 }
    }
}