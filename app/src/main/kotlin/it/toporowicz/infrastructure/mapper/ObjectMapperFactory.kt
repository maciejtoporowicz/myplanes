package it.toporowicz.infrastructure.mapper

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.micronaut.context.annotation.Factory
import javax.inject.Singleton

@Factory
class ObjectMapperFactory {
    @Singleton
    fun create(): ObjectMapper {
        return ObjectMapper(
                com.fasterxml.jackson.databind.ObjectMapper()
                        .registerModule(KotlinModule.Builder().nullIsSameAsDefault(true).build())
                        .registerModule(JavaTimeModule())
                        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        )
    }
}