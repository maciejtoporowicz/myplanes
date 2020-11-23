package it.toporowicz

import it.toporowicz.features.flightdata.core.coordinates.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class CoordinatesBoundaryCreatorTest {
    @Test
    fun test_1() {
        val boundaryOffset = Distance.kilometers(BigDecimal("124.8"))

        val actualCoords = CoordinatesBoundaryCreator().create(
            Coordinates(
                DecimalDegrees(BigDecimal("53.320555")),
                DecimalDegrees(BigDecimal("-1.729722"))
            ),
            boundaryOffset,
            boundaryOffset,
            boundaryOffset,
            boundaryOffset
        )

        val expectedCoords = CoordinatesBoundary(
            DecimalDegrees(BigDecimal("54.442908")),
            DecimalDegrees(BigDecimal("0.14877164")),
            DecimalDegrees(BigDecimal("52.198199")),
            DecimalDegrees(BigDecimal("-3.6082157"))
        )

        assertThat(actualCoords).isEqualTo(expectedCoords)
    }
}