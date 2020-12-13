package it.toporowicz.domain.radar.api

import ch.obermuhlner.math.big.BigDecimalMath
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

val mc = MathContext(8, RoundingMode.HALF_UP)

val EARTH_RADIUS = Distance(BigDecimal(6371000))

data class DecimalDegrees(val value: BigDecimal) {
    fun asRadians(): Radians {
        return Radians(
            value
                .multiply(BigDecimal(Math.PI), mc)
                .divide(BigDecimal(180), mc)
        )
    }
}

data class Radians(val value: BigDecimal) {
    fun asDecimalDegrees(): DecimalDegrees {
        return DecimalDegrees(
            value
                .multiply(BigDecimal(180), mc)
                .divide(BigDecimal(Math.PI), mc)
        )
    }
}

data class Coordinates(
        val latitude: DecimalDegrees,
        val longitude: DecimalDegrees
) {
    fun addDistance(distance: Distance, bearing: DecimalDegrees): Coordinates {
        val mc = MathContext(8, RoundingMode.HALF_UP)

        val angularDistance = distance.meters.divide(EARTH_RADIUS.meters, mc)

        val newLatitude = Radians(
            BigDecimalMath.asin(
                (BigDecimalMath.sin(this.latitude.asRadians().value, mc)
                    .multiply(BigDecimalMath.cos(angularDistance, mc), mc))
                    .add(
                        BigDecimalMath.cos(this.latitude.asRadians().value, mc)
                            .multiply(BigDecimalMath.sin(angularDistance, mc), mc)
                            .multiply(BigDecimalMath.cos(bearing.asRadians().value, mc)),
                        mc
                    ),
                mc
            )
        )
        val newLongitude = Radians(
            this.longitude.asRadians().value
                .add(
                    BigDecimalMath.atan2(
                        BigDecimalMath.sin(bearing.asRadians().value, mc)
                            .multiply(BigDecimalMath.sin(angularDistance, mc), mc)
                            .multiply(BigDecimalMath.cos(this.latitude.asRadians().value, mc), mc),
                        BigDecimalMath.cos(angularDistance, mc)
                            .subtract(
                                BigDecimalMath.sin(this.latitude.asRadians().value, mc)
                                    .multiply(BigDecimalMath.sin(newLatitude.value, mc), mc),
                                mc
                            ), mc
                    ), mc
                )
        )

        val newLongitudeNormalised = DecimalDegrees(
            (newLongitude.asDecimalDegrees().value + BigDecimal(540)) % BigDecimal(360) - BigDecimal(180)
        )

        return Coordinates(
            newLatitude.asDecimalDegrees(),
            newLongitudeNormalised
        )
    }
}