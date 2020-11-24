package it.toporowicz.coordinates

import java.math.BigDecimal
import org.junit.Test
import org.assertj.core.api.Assertions.assertThat

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