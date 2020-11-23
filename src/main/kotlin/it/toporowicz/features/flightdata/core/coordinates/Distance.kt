package it.toporowicz.features.flightdata.core.coordinates

import java.math.BigDecimal

data class Distance(val meters: BigDecimal) {
    companion object Factory {
        fun kilometers(kilometers: BigDecimal): Distance {
            return Distance(kilometers * BigDecimal.valueOf(1000))
        }

        fun meters(meters: BigDecimal): Distance {
            return Distance(meters)
        }
    }
}

