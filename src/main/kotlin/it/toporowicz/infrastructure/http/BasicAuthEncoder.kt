package it.toporowicz.infrastructure.http

import java.util.*

class BasicAuthEncoder {
    fun getBasicAuthHeaderValueFor(username: String, password: String): String {
        return Base64.getEncoder().encodeToString("${username}:${password}".toByteArray())
    }
}