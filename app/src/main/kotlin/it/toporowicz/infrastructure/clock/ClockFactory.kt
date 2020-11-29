package it.toporowicz.infrastructure.clock

import io.micronaut.context.annotation.Factory
import java.time.Clock
import javax.inject.Singleton

@Factory
class ClockFactory {
    @Singleton
    fun create(): Clock {
        return Clock.systemDefaultZone()
    }
}