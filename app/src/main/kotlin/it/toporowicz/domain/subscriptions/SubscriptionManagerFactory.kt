package it.toporowicz.domain.subscriptions

import com.google.cloud.firestore.Firestore
import io.micronaut.context.annotation.Factory
import it.toporowicz.domain.subscriptions.adapter.FirebaseBasedSubscriptionRepo
import it.toporowicz.domain.subscriptions.core.SubscriptionModule
import javax.inject.Singleton

@Factory
class SubscriptionManagerFactory {
    @Singleton
    fun create(firestore: Firestore): SubscriptionModule {
        return SubscriptionModule(FirebaseBasedSubscriptionRepo(firestore))
    }
}