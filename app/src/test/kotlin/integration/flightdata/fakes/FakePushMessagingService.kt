package integration.flightdata.fakes

import it.toporowicz.infrastructure.pushmessaging.Message
import it.toporowicz.infrastructure.pushmessaging.PushMessagingService

class FakePushMessagingService : PushMessagingService {
    private var lastSentMessage: Pair<String, Message>? = null

    fun getLastSentNotification(): Pair<String, Message>? {
        return lastSentMessage
    }

    override fun upsertTokenFor(clientId: String, newToken: String) {
        TODO("Not yet implemented")
    }

    override fun sendMessageToTopic(topic: String, message: Message) {
        lastSentMessage = topic to message
    }
}