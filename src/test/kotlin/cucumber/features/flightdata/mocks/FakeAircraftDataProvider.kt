package cucumber.features.flightdata.mocks

import it.toporowicz.features.flightdata.core.Icao24
import it.toporowicz.features.flightdata.core.aircrafts.AircraftData
import it.toporowicz.features.flightdata.core.aircrafts.AircraftDataProvider

class FakeAircraftDataProvider : AircraftDataProvider {
    private var aircraftDataItems: Map<Icao24, AircraftData> = emptyMap()

    override fun getDataBy(icao24s: Set<Icao24>): Map<Icao24, AircraftData> {
        return this.aircraftDataItems.filterKeys { icao24s.contains(it) }
    }

    fun set(aircraftData: Set<AircraftData>) {
        this.aircraftDataItems = aircraftData.associateBy { it.icao24 }
    }
}