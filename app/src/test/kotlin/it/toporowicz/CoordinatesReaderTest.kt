package it.toporowicz

import it.toporowicz.delivery.flightdata.config.CoordinatesReader
import it.toporowicz.coordinates.Coordinates
import it.toporowicz.coordinates.DecimalDegrees
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class CoordinatesReaderTest {
    @Test
    fun shouldCreateCoordinates_1() {
        val actual = CoordinatesReader().from("38.889806, -77.009056")

        val expected = Coordinates(
            DecimalDegrees(BigDecimal("38.889806")),
            DecimalDegrees(BigDecimal("-77.009056"))
        )

        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun shouldCreateCoordinates_2() {
        val actual = CoordinatesReader().from("51.096848, 16.908895")

        val expected = Coordinates(
            DecimalDegrees(BigDecimal("51.096848")),
            DecimalDegrees(BigDecimal("16.908895"))
        )

        assertThat(actual).isEqualTo(expected)
    }
}