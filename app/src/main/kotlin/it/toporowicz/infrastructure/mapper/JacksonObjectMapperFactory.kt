package it.toporowicz.infrastructure.mapper

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.databind.ObjectMapper
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import io.micronaut.jackson.JacksonConfiguration
import io.micronaut.jackson.ObjectMapperFactory
import javax.inject.Singleton

@Factory
@Replaces(ObjectMapperFactory::class)
class JacksonObjectMapperFactory(private val objectMapper: it.toporowicz.infrastructure.mapper.ObjectMapper) : ObjectMapperFactory() {
    @Singleton
    @Replaces(ObjectMapper::class)
    override fun objectMapper(jacksonConfiguration: JacksonConfiguration?, jsonFactory: JsonFactory?): ObjectMapper {
        return objectMapper.getWrappedJacksonMapper()
    }
}