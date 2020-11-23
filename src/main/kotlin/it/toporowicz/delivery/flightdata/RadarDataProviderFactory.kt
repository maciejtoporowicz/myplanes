package it.toporowicz.delivery.flightdata

import io.micronaut.context.annotation.ConfigurationProperties
import io.micronaut.context.annotation.Factory
import it.toporowicz.features.flightdata.adapter.radarData.Credentials
import it.toporowicz.features.flightdata.adapter.radarData.OpenSkyApiRadarDataProvider
import it.toporowicz.features.flightdata.core.radarData.RadarDataProvider
import it.toporowicz.infrastructure.http.BasicAuthEncoder
import it.toporowicz.infrastructure.mapper.ObjectMapper
import javax.inject.Singleton
import javax.validation.constraints.NotNull

@ConfigurationProperties("openskyapi")
interface OpenSkyApiConfig {
    @get:NotNull
    val user: String
    @get:NotNull
    val password: String
}

@Factory
internal class OpenSkyApiRadarDataProviderFactory {
    @Singleton
    fun create(openSkyApiConfig: OpenSkyApiConfig, objectMapper: ObjectMapper): RadarDataProvider {
        return OpenSkyApiRadarDataProvider(
                Credentials(
                        openSkyApiConfig.user,
                        openSkyApiConfig.password
                ),
                BasicAuthEncoder(),
                objectMapper
        )
    }
}