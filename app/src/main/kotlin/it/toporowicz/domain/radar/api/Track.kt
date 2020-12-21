package it.toporowicz.domain.radar.api

import java.math.BigDecimal

data class Track(val value: BigDecimal) {
    init {
        require(value >= BigDecimal.valueOf(0)) { "Value must be greater than 0, but was $value" }
        require(value <= BigDecimal.valueOf(360)) { "Value must be lower than 360, but was $value" }
    }
}