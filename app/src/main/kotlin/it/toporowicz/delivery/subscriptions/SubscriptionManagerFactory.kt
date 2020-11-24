package it.toporowicz.delivery.subscriptions

import com.google.cloud.firestore.Firestore
import io.micronaut.context.annotation.Factory
import it.toporowicz.features.notifications.adapter.subscriptions.FirebaseBasedSubscriptionManager
import it.toporowicz.features.notifications.core.subscriptions.SubscriptionManager
import it.toporowicz.infrastructure.firebase.FirebaseBasedJobConfigProvider
import javax.inject.Singleton

@Factory
class SubscriptionManagerFactory {
    @Singleton
    fun create(firestore: Firestore, firebaseBasedJobConfigProvider: FirebaseBasedJobConfigProvider): SubscriptionManager {
        return FirebaseBasedSubscriptionManager(
                firestore,
                firebaseBasedJobConfigProvider
        )
    }
}