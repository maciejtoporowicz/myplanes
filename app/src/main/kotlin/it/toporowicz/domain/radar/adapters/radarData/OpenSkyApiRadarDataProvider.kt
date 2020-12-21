package it.toporowicz.domain.radar.adapters.radarData

import it.toporowicz.domain.radar.api.DecimalDegrees
import it.toporowicz.domain.radar.core.coordinates.CoordinatesBoundary
import it.toporowicz.domain.radar.api.Distance
import it.toporowicz.domain.radar.api.Track
import it.toporowicz.domain.radar.ports.radarData.RadarData
import it.toporowicz.domain.radar.ports.radarData.RadarDataProvider
import it.toporowicz.infrastructure.mapper.ObjectMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.math.BigDecimal
import java.net.HttpURLConnection
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.*

data class ApiConfig(val url: String, val username: String, val password: String)

private data class OpenSkyApiStateVectors(val time: Long, val states: List<List<String?>>? = emptyList())

class OpenSkyApiRadarDataProvider(
        private val apiConfig: ApiConfig,
        private val objectMapper: ObjectMapper
) : RadarDataProvider {
    companion object {
        val log: Logger = LoggerFactory.getLogger(this::class.java)
    }

    override fun getRadarDataWithin(coordinatesBoundary: CoordinatesBoundary, maxAltitude: Distance): Set<RadarData> {
        val client = HttpClient.newHttpClient()

        val url = "${apiConfig.url}/api/states/all" +
                "?lamin=${coordinatesBoundary.latitudeMin.value}" +
                "&lomin=${coordinatesBoundary.longitudeMin.value}" +
                "&lamax=${coordinatesBoundary.latitudeMax.value}" +
                "&lomax=${coordinatesBoundary.longitudeMax.value}"

        val encodedCredentials = encodeCredentials(apiConfig.username, apiConfig.password)

        val request: HttpRequest = HttpRequest.newBuilder()
                .header("Authorization", "Basic $encodedCredentials")
                .uri(URI.create(url))
                .build()

        val stateVectors = client.send(request, HttpResponse.BodyHandlers.ofString())
                .let {
                    if(it.statusCode() != HttpURLConnection.HTTP_OK) {
                        throw RuntimeException("Request failed: $it")
                    }
                    objectMapper.fromString<OpenSkyApiStateVectors>(it.body())
        }

        log.info(request.toString())

        if (stateVectors.states == null) {
            return emptySet()
        }

        return stateVectors.states
                .map { state ->
                    RadarData(
                            icao24 = state[0]!!,
                            callSign = nullStringToNullValue(state[1])?.trim(),
                            barometricAltitude = nullStringToNullValue(state[7])?.toBigDecimalOrNull()?.let { alt -> Distance.meters(alt) },
                            onGround = nullStringToNullValue(state[8])?.toBoolean(),
                            longitude = nullStringToNullValue(state[5])?.let { DecimalDegrees(BigDecimal(it)) },
                            latitude = nullStringToNullValue(state[6])?.let { DecimalDegrees(BigDecimal(it)) },
                            track = nullStringToNullValue(state[10])?.let { Track(BigDecimal(it)) }
                    )
                }
                .filter(altitudeUnknownOrLessThan(maxAltitude))
                .toSet()
    }

    private fun encodeCredentials(username: String, password: String): String {
        return Base64.getEncoder().encodeToString("${username}:${password}".toByteArray())
    }

    private fun altitudeUnknownOrLessThan(maxAltitude: Distance): (radarData: RadarData) -> Boolean = { radarDataItem ->
        val altitude = radarDataItem.barometricAltitude
        altitude == null || altitude.meters <= maxAltitude.meters
    }

    private val nullStringToNullValue: (string: String?) -> String? = { string -> if (string == null || string == "null") null else string }
}