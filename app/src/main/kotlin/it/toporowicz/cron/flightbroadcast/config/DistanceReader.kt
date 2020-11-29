package it.toporowicz.cron.flightbroadcast.config

import it.toporowicz.domain.flightdata.api.Distance
import java.math.BigDecimal
import javax.inject.Singleton

@Singleton
class DistanceReader {
    fun from(string: String): Distance {
        return Distance.kilometers(BigDecimal(string))
    }
}
