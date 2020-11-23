package it.toporowicz.features.flightdata.adapter.radarData

import it.toporowicz.features.flightdata.core.coordinates.CoordinatesBoundary
import it.toporowicz.features.flightdata.core.coordinates.Distance
import it.toporowicz.features.flightdata.core.radarData.RadarData
import it.toporowicz.features.flightdata.core.radarData.RadarDataProvider
import it.toporowicz.infrastructure.http.BasicAuthEncoder
import it.toporowicz.infrastructure.mapper.ObjectMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.HttpURLConnection
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse


data class Credentials(val username: String, val password: String)

private data class OpenSkyApiStateVectors(val time: Long, val states: List<List<String?>>? = emptyList())

fun altitudeUnknownOrLessThan(maxAltitude: Distance): (radarData: RadarData) -> Boolean =
    fun(openApiRadarData: RadarData): Boolean {
        return openApiRadarData.barometricAltitude == null || openApiRadarData.barometricAltitude.meters <= maxAltitude.meters
    }

class OpenSkyApiRadarDataProvider(
        private val credentials: Credentials,
        private val basicAuthEncoder: BasicAuthEncoder,
        private val objectMapper: ObjectMapper
) : RadarDataProvider {
    companion object {
        val log: Logger = LoggerFactory.getLogger(this::class.java)
    }

    override fun getRadarDataWithin(coordinatesBoundary: CoordinatesBoundary, maxAltitude: Distance): Set<RadarData> {
        val client = HttpClient.newHttpClient()

        val url = "https://opensky-network.org/api/states/all" +
                "?lamin=${coordinatesBoundary.latitudeMin.value}" +
                "&lomin=${coordinatesBoundary.longitudeMin.value}" +
                "&lamax=${coordinatesBoundary.latitudeMax.value}" +
                "&lomax=${coordinatesBoundary.longitudeMax.value}"

        val basicAuthHeaderValue =
            basicAuthEncoder.getBasicAuthHeaderValueFor(credentials.username, credentials.password)

        val request: HttpRequest = HttpRequest.newBuilder()
                .header("Authorization", basicAuthHeaderValue)
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
                            callSign = state[1],
                            barometricAltitude = state[7]?.toBigDecimalOrNull()?.let { alt -> Distance.meters(alt) },
                            onGround = if (state[8] == null) null else state[8]!!.toBoolean()
                    )
                }
                .filter(altitudeUnknownOrLessThan(maxAltitude))
                .toSet()
    }
}