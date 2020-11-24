package it.toporowicz.infrastructure.firebase

import com.google.cloud.firestore.DocumentReference
import com.google.cloud.firestore.DocumentSnapshot
import com.google.cloud.firestore.Firestore
import com.google.firebase.messaging.FirebaseMessaging
import javax.inject.Singleton

@Singleton
class FirebaseMessagingManager(private val firestore: Firestore, private val firebaseMessaging: FirebaseMessaging) {
    fun upsertTokenFor(clientId: String, newToken: String) {
        val clientRef = firestore
                .collection("clients")
                .document(clientId)

        val clientSnapshot = clientRef.get().get()

        if (!clientSnapshot.exists()) {
            throw RuntimeException("Client with id=[$clientId] does not exist")
        }

        val oldToken = clientSnapshot.get("token")
        val subscriptions = getSubscriptionsFrom(clientSnapshot)

        if(oldToken != null) {
            subscriptions.forEach { subscription -> firebaseMessaging.unsubscribeFromTopic(listOf(newToken), subscription) }
        }

        subscriptions.forEach { subscription -> firebaseMessaging.subscribeToTopic(listOf(newToken), subscription) }

        val newData = HashMap(clientSnapshot.data)
                .plus("token" to newToken)

        clientRef.set(newData).get()
    }

    private fun getSubscriptionsFrom(clientSnapshot: DocumentSnapshot): Set<String> {
        val subscriptionsRefs = clientSnapshot.get("subscriptions") as Iterable<*>? ?: return emptySet()

        return subscriptionsRefs
                .map { ref -> (ref as DocumentReference).id }
                .toSet()
    }
}
