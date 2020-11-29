package integration.flightdata.mocks

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.options
import it.toporowicz.domain.flightdata.ports.radarData.RadarData
import java.net.HttpURLConnection

class OpenSkyApiMock(private val username: String, private val password: String) {
    private val wireMockServer = WireMockServer(options().dynamicPort())

    init {
        wireMockServer.start()
    }

    fun port(): Int {
        return wireMockServer.port()
    }

    fun stop() {
        wireMockServer.stop()
    }

    fun givenRadarData(
            radarDataItems: Set<RadarData>,
            latMin: String,
            latMax: String,
            lonMin: String,
            lonMax: String
    ) {
        val url = "/api/states/all?lamin=$latMin&lomin=$lonMin&lamax=$latMax&lomax=$lonMax"
        val stateObjects = if(radarDataItems.isEmpty())
            "null" else
            radarDataItems.joinToString(separator = ",", prefix = "[", postfix = "]") { stateArrayObjectFor(it) }
        val body =
                """
            {
                "time": 1600721300,
                "states": $stateObjects
            }
            """.trimIndent()

        wireMockServer.stubFor(get(urlEqualTo(url))
                .withBasicAuth(username, password)
                .willReturn(aResponse()
                        .withStatus(HttpURLConnection.HTTP_OK)
                        .withBody(body)
                )
        )
    }

    private fun stateArrayObjectFor(radarData: RadarData): String {
        return """
            [
                "${radarData.icao24}",
                ${radarData.callSign?.let { "\"$it   \"" } ?: "null"},
                "United States",
                1600721299,
                1600721299,
                -96.0514,
                36.2229,
                ${radarData.barometricAltitude?.meters?.toString() ?: "null"},
                ${radarData.onGround?.toString() ?: "null"},
                87.55,
                276.75,
                1.95,
                null,
                739.14,
                null,
                false,
                0
            ]
        """.trimIndent()
    }
}