package it.toporowicz.infrastructure.pushmessaging

interface PushMessagingService {
    fun upsertTokenFor(clientId: String, newToken: String)

    fun sendMessageToTopic(topic: String, message: Message)
}