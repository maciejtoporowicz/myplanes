package it.toporowicz.delivery.firebase.rest

import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Put
import it.toporowicz.infrastructure.firebase.FirebaseMessagingManager

data class FcmToken (val token: String)

@Controller("/firebase/client")
class FirebaseSubscriptionController(private val firebaseMessagingManager: FirebaseMessagingManager) {
    @Put("/{clientId}/token")
    fun saveToken(clientId: String, @Body fcmToken: FcmToken) {
        firebaseMessagingManager.upsertTokenFor(clientId, fcmToken.token)
    }
}