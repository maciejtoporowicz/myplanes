package it.toporowicz.domain.subscriptions

import com.google.cloud.firestore.Firestore
import io.micronaut.context.annotation.Factory
import it.toporowicz.domain.subscriptions.adapter.FirebaseBasedSubscriptionRepo
import javax.inject.Singleton

@Factory
class SubscriptionManagerFactory {
    @Singleton
    fun create(firestore: Firestore): FirebaseBasedSubscriptionRepo {
        return FirebaseBasedSubscriptionRepo(firestore)
    }
}