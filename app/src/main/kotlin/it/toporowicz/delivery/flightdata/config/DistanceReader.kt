package it.toporowicz.delivery.flightdata.config

import it.toporowicz.coordinates.Distance
import java.math.BigDecimal
import javax.inject.Singleton

@Singleton
class DistanceReader {
    fun from(string: String): Distance {
        return Distance.kilometers(BigDecimal(string))
    }
}