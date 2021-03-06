package it.toporowicz.infrastructure.mapper

import com.fasterxml.jackson.core.type.TypeReference

class ObjectMapper(val jacksonMapper: com.fasterxml.jackson.databind.ObjectMapper) {
    inline fun <reified T> fromString(string: String): T {
        return jacksonMapper.readValue(string, object : TypeReference<T>() {})
    }

    fun <T> toJson(obj: T): String {
        return jacksonMapper.writeValueAsString(obj)
    }

    fun getWrappedJacksonMapper(): com.fasterxml.jackson.databind.ObjectMapper {
        return jacksonMapper
    }
}