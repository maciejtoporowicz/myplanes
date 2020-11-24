package it.toporowicz.delivery.flightdata.config

import it.toporowicz.coordinates.Coordinates
import it.toporowicz.coordinates.DecimalDegrees
import java.math.BigDecimal
import java.util.regex.Pattern
import javax.inject.Singleton

@Singleton
class CoordinatesReader {
    fun from(stringValue: String): Coordinates {
        val pattern = Pattern.compile("(-?\\d+\\.\\d+)\\s*+,\\s*+(-?\\d+\\.\\d+)")
        val matcher = pattern.matcher(stringValue)

        if (!matcher.matches()) {
            throw RuntimeException("Could not parse [$stringValue] as coordinates")
        }

        val latitude = DecimalDegrees(BigDecimal(matcher.group(1)))
        val longitude = DecimalDegrees(BigDecimal(matcher.group(2)))

        return Coordinates(latitude, longitude)
    }
}