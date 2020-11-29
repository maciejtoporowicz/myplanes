package it.toporowicz.application.client

import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Put
import it.toporowicz.infrastructure.pushmessaging.PushMessagingService

data class Token (val token: String)

@Controller("/config/client")
class ClientConfigController(private val pushMessagingService: PushMessagingService) {
    @Put("/{clientId}/token")
    fun savePushMessagingToken(clientId: String, @Body token: Token) {
        pushMessagingService.upsertTokenFor(clientId, token.token)
    }
}