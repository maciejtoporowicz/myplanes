package integration.flightdata.fakes

import io.micronaut.context.event.ApplicationEventPublisher
import it.toporowicz.infrastructure.pushmessaging.Message

class FakeApplicationEventPublisher : ApplicationEventPublisher {
    private var lastEvent: Any? = null

    fun getLastSentEvent(): Any? {
        return lastEvent
    }

    override fun publishEvent(event: Any) {
        lastEvent = event
    }
}